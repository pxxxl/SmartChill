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

export const getCameraAPI = (data) => {
    return request({
        url: "/admin/device/camera",
        method: "get",
        data,
    });
}