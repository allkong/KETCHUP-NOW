<script setup>
import { message } from "ant-design-vue";
import { inject, onMounted, ref } from "vue"
import { useRoute, useRouter } from "vue-router";

const axios = inject('axios')
const route = useRoute()
const router = useRouter()

const reviewFormState = ref({
    title: '',
    content: '',
    score: 0
})

const targetStory = ref({
    title: '',
    description: '',
})

const playing = ref({
    startedAt: '',
    clearedAt: '',
    playingTime: 0
})

async function onSubmitForm() {
    if (!reviewFormState.value.title) {
        message.error('제목을 입력해주세요!')
        return
    }

    axios.post(`/stories/${route.params.storyUuid}/reviews`, reviewFormState.value)
    .then(() => {
        message.success('소중한 리뷰 감사합니다!')
        router.push({name: 'story:cleared-list'})
    })
    .catch(error => {
        message.error('문제가 발생했어요, 다시 시도해 주세요.')
        console.log(error);
    })
}

onMounted(async () => {
    const playings = (await axios.get('/playings')).data
    // 클레이한 적이 없으면 리뷰를 남길 수 없음
    if (playings.filter(playing => playing.storyUuid === route.params.storyUuid && playing.status === 'CLEARED').length === 0) {
        router.back()
    }

    playing.value = playings.filter(playing => playing.storyUuid === route.params.storyUuid && playing.status === 'CLEARED')[0]
    playing.value.playingTime = Math.ceil(((new Date(playing.value.clearedAt)).getTime() - (new Date(playing.value.startedAt)).getTime()) / (1000 * 60))

    targetStory.value = (await axios.get(`/stories/${playing.value.storyUuid}`)).data

    // 화면 진입 시 점수 오르내리는 애니메이션 구현
    const scoreIncreaseInterval = setInterval(() => {
        reviewFormState.value.score += 1
        if (reviewFormState.value.score === 5) {
            stopIncreaseInterval()
        }
    }, 50)
    let scoreDecreaseInterval = null
    const stopIncreaseInterval = () => {
        clearInterval(scoreIncreaseInterval)
        setTimeout(() => {
            scoreDecreaseInterval = setInterval(() => {
                reviewFormState.value.score -= 1
                if (reviewFormState.value.score === 0) {
                    stopDecreaseInterval()
                }
            }, 50)  
        }, 500)
    }
    const stopDecreaseInterval = () => {clearInterval(scoreDecreaseInterval)}
})
</script>

<template>
    <div id="container">
        <h1>
            <img class="logo-img" src="@/assets/logo.png" alt="logo-img" />
            여행은 즐거우셨나요?
        </h1>
        <section>리뷰를 남겨 여행자님의 추억을 공유해주세요!</section>
        <section class="section-container">
            <h2 style="font-size:medium; text-align: center;">클리어 정보</h2>
            <a-row class="clear-info-row">
                <a-col class="clear-info-label-col" span="6">제목</a-col>
                <a-col span="18"><a-input :value="targetStory.title" readonly/></a-col>
            </a-row>
            <a-row class="clear-info-row">
                <a-col class="clear-info-label-col" span="6">줄거리</a-col>
                <a-col span="18"><a-textarea :value="targetStory.description" readonly :rows="4" /></a-col>
            </a-row>
            <a-row class="clear-info-row">
                <a-col class="clear-info-label-col" span="6">소요 시간</a-col>
                <a-col span="18"><a-input :value="playing.playingTime + '분'" readonly :rows="4" /></a-col>
            </a-row>
        </section>
        <section class="section-container">
            <a-form :model="reviewFormState" @submit.prevent="onSubmitForm" >
                  <a-rate v-model:value="reviewFormState.score" />
                  <a-input v-model:value="reviewFormState.title" placeholder="제목" style="margin-bottom: 0.5rem" />
                  <a-textarea v-model:value="reviewFormState.content" placeholder="내용" :rows="8" />
                  <a-button @click="onSubmitForm">작성 완료!</a-button>
            </a-form>
        </section>
    </div>
</template>

<style scoped>
#container {
    display: flex;
    flex-direction: column;
    align-items: center;
}
h1 {
    padding-top: 2rem;
    display: flex;
    flex-direction: column;
    align-items: center
}
h2 {
    margin-top: 0;
}
.logo-img {
  width: 3rem;
  height: 3rem;
  margin: 0.5rem;
}
.section-container {
    width: 90vw;
    margin-top: 1rem;
    padding: 1rem 1rem;
    border: 1px solid silver;
    border-radius: 0.5rem;
}
.ant-form {
    text-align: center;
}
.ant-rate {
    margin-bottom: 0.5rem;
    color: tomato;
}
.ant-btn {
    margin-top: 0.5rem;
    width: 100%;
    background-color: tomato;
    color: white
}
.clear-info-row {
    display: flex;
    align-items: center;
    margin-bottom: 0.5rem;
}
.clear-info-label-col {
    text-align: right;
    padding-right: 1rem;
}

</style>