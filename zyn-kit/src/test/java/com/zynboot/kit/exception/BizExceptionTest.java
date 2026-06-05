package com.zynboot.kit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BizExceptionTest {

    @Test
    void shouldCreateNotFoundException() {
        BizException ex = BizException.notFound("用户");

        assertThat(ex.getStatus()).isEqualTo(404);
        assertThat(ex.getCode()).isEqualTo("NOT_FOUND");
        assertThat(ex.getMessage()).isEqualTo("用户不存在");
    }

    @Test
    void shouldCreateForbiddenException() {
        BizException ex = BizException.forbidden("无权限访问");

        assertThat(ex.getStatus()).isEqualTo(403);
        assertThat(ex.getCode()).isEqualTo("FORBIDDEN");
        assertThat(ex.getMessage()).isEqualTo("无权限访问");
    }

    @Test
    void shouldCreateUnauthorizedException() {
        BizException ex = BizException.unauthorized("未登录");

        assertThat(ex.getStatus()).isEqualTo(401);
        assertThat(ex.getCode()).isEqualTo("UNAUTHORIZED");
        assertThat(ex.getMessage()).isEqualTo("未登录");
    }

    @Test
    void shouldCreateConflictException() {
        BizException ex = BizException.conflict("编码已存在");

        assertThat(ex.getStatus()).isEqualTo(409);
        assertThat(ex.getCode()).isEqualTo("CONFLICT");
        assertThat(ex.getMessage()).isEqualTo("编码已存在");
    }

    @Test
    void shouldCreateBadRequestException() {
        BizException ex = BizException.badRequest("参数错误");

        assertThat(ex.getStatus()).isEqualTo(400);
        assertThat(ex.getCode()).isEqualTo("BAD_REQUEST");
        assertThat(ex.getMessage()).isEqualTo("参数错误");
    }

    @Test
    void shouldBeInstanceofBaseException() {
        BizException ex = BizException.notFound("资源");
        assertThat(ex).isInstanceOf(BaseException.class);
    }
}
