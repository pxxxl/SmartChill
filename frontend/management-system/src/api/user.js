import request from "@/utils/request";

export const registerAPI = (data) => {
    return request({
        url: "/admin/register",
        method: "post",
        data,
    });
}

export const loginAPI = (data) => {
    return request({
        url: "/admin/login",
        method: "post",
        data,
    });
}  