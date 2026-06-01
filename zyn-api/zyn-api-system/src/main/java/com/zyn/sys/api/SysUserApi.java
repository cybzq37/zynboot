package com.zyn.sys.api;
import com.zyn.infra.exchange.ExchangeClient;

import com.zyn.kit.response.ApiResponse;
import com.zyn.sys.command.user.UserSaveCmd;
import com.zyn.sys.query.user.UserPageQuery;
import com.zyn.sys.response.PageRes;
import com.zyn.sys.response.user.UserRes;
import com.zyn.infra.exchange.query.HttpQuery;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;

@ExchangeClient("sys")
@HttpExchange("/sys/api/v1/user")
public interface SysUserApi {

    @GetExchange
    ApiResponse<PageRes<UserRes>> page(@HttpQuery UserPageQuery query);

    @GetExchange("/{id}")
    ApiResponse<UserRes> getById(@PathVariable String id);

    @PostExchange
    ApiResponse<Void> create(@RequestBody UserSaveCmd cmd);

    @PutExchange("/{id}")
    ApiResponse<Void> update(@PathVariable String id, @RequestBody UserSaveCmd cmd);

    @DeleteExchange("/{id}")
    ApiResponse<Void> delete(@PathVariable String id);
}
