# 🌐 Web Client – e-Parousiologio (Πλευρά Φοιτητή)

## 📖 Περιγραφή
Αυτός είναι ο web client του ηλεκτρονικού παρουσιολογίου.  
Στόχος αυτού του τμήματος της εφαρμογής είναι να παρέχει την δυνατότητα στους φοιτητές</br>
να καταχωρούν τα ΑΕΜ τους στο σύστημα με σκοπό να δηλώνεται η παρουσία τους στο αντίστοιχο εργαστηριακό τμήμα.

## 🖼️ Οδηγίες Χρήσης

### Άνοιγμα ιστοσελίδας & Πρόσβαση Τοποθεσίας

Δίνετε την άδεια πρόσβασης της τοποθεσίας της συσκευής σας στην εφαρμογή για να υπάρχει έλεγχος εμβέλειας του ατόμου σε σύγκριση με την τοποθεσία της σχολης.</br>
<b>ΣΗΜΕΙΩΣΗ:</b> Τα γεωγραφικά σας δεδομένα <b>ΔΕΝ</b> καταγράφονται, χρησιμοποιούνται <b>ΜΟΝΟ</b> κατά την χρήση της εφαρμογής.

| Light Mode | Dark Mode |
|------------|-----------|
| <img src="./screenshots/location%20access%20light%201.png" width="300" /> | <img src="./screenshots/location%20access%20dark%201.png" width="300" /> |

---

### Άρνηση άδειας πρόσβασης τοποθεσίας

- Εάν αρνηθείτε να δώσετε άδεια της τοποθεσίας της συσκευής σας, λαμβάνετε σχετικό μήνυμα στην οθόνη και δεν έχετε την δυνατότητα να δηλώσετε παρουσία στο τρέχον μάθημα.
- Εάν εγκρίνετε την άδεια της τοποθεσίας της συσκευής σας αλλά είστε εκτός της ζώνης εμβέλειας του τμήματος, λαμβάνετε σχετικό μήνυμα στην οθόνη και δεν έχετε την δυνατότητα να δηλώσετε παρουσία στο τρέχον μάθημα.

| Light Mode | Dark Mode |
|------------|-----------|
| <img src="./screenshots/location%20access%20denied%20light%202.png" width="300" /> | <img src="./screenshots/location%20access%20denied%20dark%202.png" width="300" /> |
| <img src="./screenshots/location%20access%20accepted%20far%20light%203.png" width="300" /> | <img src="./screenshots/location%20access%20accepted%20far%20dark%203.png" width="300" /> |
---

### Έγκρισης άδειας πρόσβασης τοποθεσίας

Αφού εγκρίνετε την άδεια της τοποθεσίας και επαληθευτούν τα γεωγραφικα δεδομένα θα εμφανιστεί στην οθόνη σας ένα πλαίσιο για να εισάγετε τον ΑΕΜ σας ΕΦΟΣΟΝ ο καθηγητής έχει ενεργοποιημένη την δυνατότητα υποβολής, αυτό θα καθοριστεί κατόπιν συνεννοήσεως με τον διδάσκοντα την ώρα του μαθήματος. 

| Light Mode | Dark Mode |
|------------|-----------|
| <img src="./screenshots/location%20access%20accepted%20ok%20light%204.png" width="300" /> | <img src="./screenshots/location%20access%20accepted%20ok%20dark%204.png" width="300" /> |
| <img src="./screenshots/teacher%20allow%20attendance%20light%205.png" width="300" /> | <img src="./screenshots/teacher%20allow%20attendance%20dark%205.png" width="300" /> |
---

### Υποβολή παρουσίας

Κατά την υποβολή του ΑΕΜ σας στην φόρμα πραγματοποιείται έλεγχος ορθότητας δεδομένων, έτσι διασταυρώνεται το αν ανήκετε στο τρέχον τμήμα και λαμβάνετε το αντίστοιχο μήνυμα στην οθόνη σας για την κάθε περίπτωση.

| Light Mode | Dark Mode |
|------------|-----------|
| <img src="./screenshots/wrong%20section%20light%206.png" width="300" /> | <img src="./screenshots/wrong%20section%20dark%206.png" width="300" /> |
| <img src="./screenshots/attendance%20register%20success%20light%207.png" width="300" /> | <img src="./screenshots/attendance%20register%20success%20dark%207.png" width="300" /> |
---

## ⚙️ Τεχνολογίες
- Firebase Firestore και Firebase Hosting <a href="https://firebase.google.com/">Oικοσύστημα Google Firebase</a>
- HTML, CSS, JavaScript

---

## 📄 Άδεια
Αυτό το έργο διατίθεται με άδεια [Apache 2.0 License](../LICENSE).
