import request from '@/utils/request'

export const getTemperature = () => {
    return request.get('/user/temperature')
}

export const getDrinkInfo = (page, pageSize) => {
    return request.get(`/user/drink?page=${page}&pageSize=${pageSize}`)
}

