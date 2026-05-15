package com.restaurant.pos.api;

/**
 * Generic wrapper for API results — carries Loading / Success / Error state.
 * Used with LiveData so UI can observe a single stream and react to all states.
 */
public class ApiResult<T> {

    public enum Status { LOADING, SUCCESS, ERROR }

    public final Status status;
    public final T data;
    public final String error;

    private ApiResult(Status status, T data, String error) {
        this.status = status;
        this.data   = data;
        this.error  = error;
    }

    public static <T> ApiResult<T> loading() {
        return new ApiResult<>(Status.LOADING, null, null);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(Status.SUCCESS, data, null);
    }

    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(Status.ERROR, null, message);
    }

    public boolean isLoading()  { return status == Status.LOADING; }
    public boolean isSuccess()  { return status == Status.SUCCESS; }
    public boolean isError()    { return status == Status.ERROR; }
}
