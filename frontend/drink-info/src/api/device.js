import request from "@/utils/request";

export const getDeviceInfoAPI = () => {
    return request({
        url: "/admin/device/status",
        method: "get",
    });
}