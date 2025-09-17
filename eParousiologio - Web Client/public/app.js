// ========== Firebase (ESM) ==========
import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.0/firebase-app.js";
import { 
  getFirestore, doc, getDoc, setDoc, serverTimestamp 
} from "https://www.gstatic.com/firebasejs/10.12.0/firebase-firestore.js";

const firebaseConfig = {
  apiKey: "AIzaSyCGi9p53Y_RMSIwjPrs7V8oyKdk_YNlyTs",
  authDomain: "parousiologio-6b9c0.firebaseapp.com",
  projectId: "parousiologio-6b9c0",
  storageBucket: "parousiologio-6b9c0.appspot.com",
  messagingSenderId: "398482455434",
  appId: "1:398482455434:web:3db85dc9dd42ece2832673",
  measurementId: "G-HEZ5MM3E15"
};

const app = initializeApp(firebaseConfig);

const db = getFirestore(app);

// ========== UI/DOM ==========
const TARGET = { lat: 41.0748233, lng: 23.555335 };
const RADIUS_M = 150;
const $ = s => document.querySelector(s);

const courseNameEl = $('#courseName');
const sectionNameEl = $('#sectionName');
const aemForm = $('#aemForm'), retryBtn = $('#retryBtn'),
      statusBanner = $('#statusBanner'), statusBannerHint = $('#statusBannerHint'),
      bannerSpinner = $('#bannerSpinner'), actionBlock = $('#actionBlock');

const setStatus = (type, message, showButton = false, showSpinner = false) => {
  statusBannerHint.textContent = message;
  statusBanner.classList.remove('is-info','is-success','is-warn','is-error');
  statusBanner.classList.add(`is-${type}`);
  show(statusBanner, true);
  show(actionBlock, showButton);
  show(bannerSpinner, showSpinner);
};
const show = (el,v) => el.classList.toggle('hidden', !v);
const pad2 = x => x.toString().padStart(2, "0");

// ========== Live state ==========
async function getLiveState(){
  const snap = await getDoc(doc(db, "app_state", "live"));
  if (!snap.exists()) return null;
  const d = snap.data();
  return {
    courseId: d.courseId || null,
    courseTitle: d.courseTitle || d.courseId || "(—)",
    labId: d.labId || null,
    labName: d.labName || "(—)",
    isOpen: d.isOpen === true,
    note: d.note || "",
    openedAt: d.openedAt || null
  };
}

// ========== Geolocation helpers ==========
const isSecure = (() => {
  try { return (location.protocol === 'https:'); }
  catch { return false; }
})();
const hardTimeout = ms => new Promise((_,rej)=>setTimeout(()=>rej(new Error('timeout')),ms));
const distanceMeters = (a,b) => {
  const R=6371000,toRad=d=>d*Math.PI/180;
  const dLat=toRad(b.lat-a.lat), dLng=toRad(b.lng-a.lng);
  const s1=Math.sin(dLat/2)**2 + Math.cos(toRad(a.lat))*Math.cos(toRad(b.lat))*Math.sin(dLng/2)**2;
  return 2*R*Math.asin(Math.sqrt(s1));
};
const geoOnce = () => new Promise((res,rej)=>{
  if (!isSecure) { rej(new Error('insecure-context')); return; }
  if (!('geolocation' in navigator)) { rej(new Error('no-geo')); return; }
  navigator.geolocation.getCurrentPosition(
    p => res({ lat: p.coords.latitude, lng: p.coords.longitude }),
    e => rej(e),
    { enableHighAccuracy: true, timeout: 20000, maximumAge: 0 }
  );
});

let locating = false, watchdog = null;
function startWatchdog(ms){
  clearWatchdog();
  watchdog = setTimeout(()=>{
    setStatus('error','Η τοποθεσία άργησε να εντοπιστεί. Παρακαλούμε δοκιμάστε ξανά.', true, false);
    show(aemForm,false);
    locating=false;
  }, ms);
}
function clearWatchdog(){ if(watchdog){ clearTimeout(watchdog); watchdog=null } }

retryBtn.addEventListener('click', init);
init();

async function init(){
  try {
    setStatus('info','Έλεγχος διαθεσιμότητας…', false, true);
    show(aemForm,false);

    const live = await getLiveState();

    if (!live) {
      setStatus('warn','Δεν υπάρχει ενεργό τμήμα αυτή τη στιγμή.', false, false);
      courseNameEl.textContent = '—';
      sectionNameEl.textContent = '(κανένα)';
      return;
    }

    if (!live.courseId || !live.labId) {
      setStatus('error','Παρουσιάστηκε πρόβλημα με την ενεργή ρύθμιση τμήματος. Παρακαλούμε δοκιμάστε ξανά αργότερα.', true, false);
      return;
    }

    courseNameEl.textContent = live.courseTitle || live.courseId || '—';
    sectionNameEl.textContent = live.labName || (live.isOpen ? '(ενεργό)' : '(κλειστό)');

    if (!live.isOpen) {
      setStatus('warn','Το σύστημα παρουσιών είναι κλειστό αυτή τη στιγμή.', false, false);
      show(aemForm,false);
      return;
    }

    await runGeolocationGate();

    setStatus('success','Η τοποθεσία επαληθεύτηκε. Παρακαλούμε εισάγετε τον ΑΕΜ σας.');
    show(aemForm,true);
    show(statusBanner,false);

  } catch (e) {
    if (e && (e.message === 'insecure-context' || e.message === 'no-geo' || 
              e.message === 'timeout' || e.message === 'out-of-zone' || typeof e?.code === 'number')) {
      return;
    }

    setStatus('error', 'Παρουσιάστηκε σφάλμα κατά την αρχικοποίηση. Παρακαλούμε δοκιμάστε ξανά.', false, false);
    console.error(e);
  }
}

async function runGeolocationGate(){
  if (locating) return;
  locating = true; retryBtn.disabled = true;
  setStatus('info','Έλεγχος τοποθεσίας…', false, true);
  show(aemForm,false);
  startWatchdog(25000);

  try{
    const loc = await Promise.race([geoOnce(), hardTimeout(24000)]);
    clearWatchdog();
    const d = distanceMeters(loc, TARGET);
    if (d >= RADIUS_M) {
      setStatus('warn','Είστε εκτός επιτρεπόμενης ζώνης για δήλωση παρουσίας.', true, false);
      throw new Error('out-of-zone');
    }
  } catch (e){
    clearWatchdog();
    console.warn('Geo error:', e);

    if (e && e.message === 'insecure-context') {
      setStatus('error', 'Η τοποθεσία είναι διαθέσιμη μόνο μέσω HTTPS.', true, false);
    } else if (e && e.message === 'no-geo') {
      setStatus('error', 'Ο περιηγητής σας δεν υποστηρίζει ή έχει απενεργοποιήσει την υπηρεσία τοποθεσίας.', true, false);
    } else if (e && e.message === 'timeout') {
      setStatus('error', 'Έληξε ο χρόνος εντοπισμού. Παρακαλούμε βεβαιωθείτε ότι η τοποθεσία είναι ενεργή.', true, false);
    } else if (e && e.message === 'out-of-zone') {
      // μήνυμα έχει ήδη εμφανιστεί
    } else if (typeof e?.code === 'number') {
      if (e.code === 1) setStatus('error', 'Δεν δόθηκε άδεια πρόσβασης στην τοποθεσία. Παρακαλούμε επιτρέψτε την.', true, false);
      else if (e.code === 2) setStatus('error', 'Η υπηρεσία τοποθεσίας δεν είναι διαθέσιμη αυτή τη στιγμή.', true, false);
      else if (e.code === 3) setStatus('error', 'Έληξε ο χρόνος εντοπισμού. Παρακαλούμε δοκιμάστε ξανά.', true, false);
      else setStatus('error', 'Παρουσιάστηκε σφάλμα στην τοποθεσία. Παρακαλούμε δοκιμάστε ξανά.', true, false);
    } else {
      setStatus('error', 'Παρουσιάστηκε σφάλμα στην τοποθεσία. Παρακαλούμε δοκιμάστε ξανά.', true, false);
    }
    throw e; 
  } finally {
    locating = false; retryBtn.disabled = false;
  }
}

aemForm.addEventListener('submit', async e => {
  e.preventDefault();
  const aem = $('#aem').value.trim();

  if (!/^\d{4,12}$/.test(aem)) {
    setStatus('warn','Δώστε έγκυρο ΑΕΜ (μόνο ψηφία, 4–12).');
    return;
  }

  try {
    const live = await getLiveState();
    if (!live) { setStatus('warn','Δεν υπάρχει ενεργό τμήμα αυτή τη στιγμή.'); return; }
    if (!live.isOpen) { setStatus('warn','Το σύστημα παρουσιών είναι κλειστό αυτή τη στιγμή.'); return; }
    if (!live.courseId || !live.labId) { setStatus('error','Δεν έχει οριστεί σωστά το ενεργό τμήμα.', true); return; }

    courseNameEl.textContent = live.courseTitle || live.courseId || '—';
    sectionNameEl.textContent = live.labName || '(—)';

    // Προαιρετικός έλεγχος ότι ο ΑΕΜ ανήκει στο lab
    const studRef = doc(db, "courses", live.courseId, "labs", live.labId, "students", aem);
    const studSnap = await getDoc(studRef);
    if (!studSnap.exists()) {
      setStatus('error','Ο ΑΕΜ δεν ανήκει στο επιλεγμένο τμήμα.');
      retryBtn.classList.remove('hidden'); // δείξε το κουμπί αν αποτύχει
      return;
    }

    // SessionId σε UTC (YYYYMMDD-<labId>)
    const now = new Date();
    const yyyy = now.getUTCFullYear();
    const MM = pad2(now.getUTCMonth() + 1);
    const dd = pad2(now.getUTCDate());
    const sessionId = `${yyyy}${MM}${dd}-${live.labId}`;

    const headerRef = doc(db, "courses", live.courseId, "attendance", sessionId);
    const entryRef  = doc(db, "courses", live.courseId, "attendance", sessionId, "entries", aem);

    // Προέλεγχος: ήδη δηλωμένος;
    const already = await getDoc(entryRef);
    if (already.exists()) {
      setStatus('warn', 'Έχετε ήδη δηλώσει παρουσία για το σημερινό τμήμα.');
      retryBtn.classList.remove('hidden'); // δείξε ξανά το κουμπί
      return;
    }

    // Γράψε/merge header
    await setDoc(headerRef, {
      courseId: live.courseId,
      courseTitle: live.courseTitle || live.courseId,
      labId: live.labId,
      labName: live.labName || "",
      openedAt: serverTimestamp()
    }, { merge: true });

    // Γράψε το entry
    await setDoc(entryRef, {
      at: serverTimestamp(),
      ok: true,
      courseId: live.courseId,
      labId: live.labId,
      courseTitle: live.courseTitle || live.courseId,
      labName: live.labName || "",
      studentAEM: aem
    });

    // Επιτυχία → κρύψε το κουμπί
    setStatus('success', `Η παρουσία σας στο τμήμα "${live.labName}" καταχωρήθηκε.`);
    show(aemForm,false);
    show(actionBlock,true);
    retryBtn.classList.add('hidden');  // κρύβει το κουμπί
    aemForm.reset();

  } catch (err) {
    console.error('write error:', err?.code, err?.message);
    setStatus('error','Η καταχώριση απέτυχε. Προσπαθήστε ξανά.');
    retryBtn.classList.remove('hidden'); // δείξε το κουμπί σε σφάλμα
  }
});
