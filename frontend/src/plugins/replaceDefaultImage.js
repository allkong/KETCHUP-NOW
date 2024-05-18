import defaultImage from '@/assets/default-image.jpg'

const replaceDefaultImage = (e) => {
  console.log(e.target.src)
  e.target.src = defaultImage
}

export default {
  install(app) {
    app.config.globalProperties.$replaceDefaultImage = replaceDefaultImage
  },
}
