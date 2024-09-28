import request from "@/utils/request";

export const getDeviceInfoAPI = () => {
    return request({
        url: "/admin/device",
        method: "get",
    });
}