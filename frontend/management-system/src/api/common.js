import request from "@/utils/request";

export const uploadAPI = (data) => {
    return request({
        url: "/admin/upload",
        method: "post",
        data,
    });
}