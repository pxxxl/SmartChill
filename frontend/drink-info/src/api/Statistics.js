import request from '@/utils/request'

export const getStatistics = (begin, end) => {
    return request.get(`/statistics/sell?begin=${begin}&end=${end}`)
}