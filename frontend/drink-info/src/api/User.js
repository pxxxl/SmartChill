import request from '@/utils/request'

export const getTemperature = () => {
    return request.get('/user/temperature')
}

export const getDrinkInfo = (page, pageSize, order = true, fridge) => {
    if(fridge === undefined) {
        return request({
            url: "/user/drink?pagesize=" + pageSize + "&page=" + page + "&order=" + order,
            method: "get",
        });
    }
    return request({
        url: "/user/drink?pagesize=" + pageSize + "&page=" + page + "&order=" + order + "&fridge=" + fridge,
        method: "get",
    });
}