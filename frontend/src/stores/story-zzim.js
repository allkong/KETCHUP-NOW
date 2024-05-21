import HttpStatus from "@/api/http-status";
import { defineStore } from "pinia";
import { inject, ref } from "vue";

export const useStoryZzimStore = defineStore('storyZzimStore', () => {
    const axios = inject('axios')

    const zzimStories = ref([])

    const fetchZzimStories = async () => {
        return axios.get('/users/me/zzims')
        .then(resp => {
            zzimStories.value = resp.data
            return Promise.resolve(resp.data)
        })
        .catch(error => {
            console.log(error);
        })
    }

    const toggleZzim = async (storyUuid) => {
        return axios.post(`/stories/${storyUuid}/zzims`).then((resp) => {
            if (resp.status === HttpStatus.OK) {
                fetchZzimStories()
                return Promise.resolve(true)
            } else if (resp.status === HttpStatus.NO_CONTENT) {
                fetchZzimStories()
                return Promise.resolve(false)
            } else {
                return Promise.reject(new Error('Unknown status code ' + resp.status))
            }
        })
    }

    return {
        zzimStories,
        fetchZzimStories,
        toggleZzim,
    }
})