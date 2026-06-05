package com.zynboot.kit.exception;

/**
 * 业务异常，预定义常见错误码。
 * <p>
 * 错误码规范：
 * <ul>
 *   <li>{@code 400} — 请求参数错误</li>
 *   <li>{@code 401} — 未认证</li>
 *   <li>{@code 403} — 无权限</li>
 *   <li>{@code 404} — 资源不存在</li>
 *   <li>{@code 409} — 资源冲突（如编码重复）</li>
 *   <li>{@code 500} — 服务器内部错误</li>
 * </ul>
 */
public class BizException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BizException(String code, String message) {
        super(code, message);
    }

    public BizException(int statusCode, String code, String message) {
        super(statusCode, code, message);
    }

    public static BizException notFound(String resource) {
        return new BizException(404, "NOT_FOUND", resource + "不存在");
    }

    public static BizException forbidden(String message) {
        return new BizException(403, "FORBIDDEN", message);
    }

    public static BizException unauthorized(String message) {
        return new BizException(401, "UNAUTHORIZED", message);
    }

    public static BizException conflict(String message) {
        return new BizException(409, "CONFLICT", message);
    }

    public static BizException badRequest(String message) {
        return new BizException(400, "BAD_REQUEST", message);
    }
}
