import request from "@/utils/request";

export const supplyAPI = (data) => {
    return request({
        url: "/admin/drink",
        method: "post",
        data,
    });
}

export const getDrinkBaseInfoAPI = () => {
    return request({
        url: "/admin/drink/basic",
        method: "get",
    });
}

export const setDrinkBaseInfoAPI = (data) => {
    return request({
        url: "/admin/drink/basic",
        method: "put",
        data,
    });
}

export const getDrinkRealTimeInfoAPI = (page, pageSize) => {
    return request({
        url: "/user/drink?pagesize=" + pageSize + "&page=" + page,
        method: "get",
    });
}