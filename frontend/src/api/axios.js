import axios from 'axios'
import { httpStatus } from './http-status'

const { VITE_SERVER_URL } = import.meta.env

export const instance = axios.create({
    baseURL: VITE_SERVER_URL
})