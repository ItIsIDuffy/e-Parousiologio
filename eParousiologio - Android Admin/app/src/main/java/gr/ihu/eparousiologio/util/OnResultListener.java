package gr.ihu.eparousiologio.util;

public interface OnResultListener<T> {
    void onSuccess(T result);

    void onFailure(Exception e);
}
