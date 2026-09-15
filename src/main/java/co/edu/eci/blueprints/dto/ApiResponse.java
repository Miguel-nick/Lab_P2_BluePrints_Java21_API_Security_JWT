package co.edu.eci.blueprints.dto;

public record ApiResponse<T>(int code, String message, T data) {
    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "execute ok", data);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "resource created", data);
    }

    public static <T> ApiResponse<T> accepted(T data) {
        return new ApiResponse<>(202, "update accepted", data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}