import HttpStatus from "@/api/http-status";
import { defineStore } from "pinia";
import { inject, ref } from "vue";

export const useStoryZzimStore = defineStore('storyZzimStore', () => {
    const axios = inject('axios')

    const zzims = ref([])

    const fetchZzims = async () => {
        return axios.get('/users/me/zzims')
        .then(resp => {
            zzims.value = resp.data
            return Promise.resolve(resp.data)
        })
        .catch(error => {
            console.log(error);
        })
    }

    const toggleZzim = async (storyUuid) => {
        return axios.post(`/stories/${storyUuid}/zzims`).then((resp) => {
            if (resp.status === HttpStatus.OK) {
                fetchZzims()
                return Promise.resolve(true)
            } else if (resp.status === HttpStatus.NO_CONTENT) {
                fetchZzims()
                return Promise.resolve(false)
            } else {
                return Promise.reject(new Error('Unknown status code ' + resp.status))
            }
        })
    }

    return {
        zzims,
        fetchZzims,
        toggleZzim,
    }
})