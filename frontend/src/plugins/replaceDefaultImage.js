import defaultImage from '@/assets/default-image.jpg'

const replaceDefaultImage = (e) => {
  e.target.src = defaultImage
}

export default {
  install(app) {
    app.config.globalProperties.$replaceDefaultImage = replaceDefaultImage
  },
}
