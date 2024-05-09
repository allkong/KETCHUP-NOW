import _ from 'lodash'

const HttpStatus = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  METHOD_NOT_ALLOWED: 405,
  NOT_ACCEPTABLE: 406,
  CONFLICT: 409,
  UNSUPPORTED_MEDIA_TYPE: 415,
  INTERNAL_SERVER_ERROR: 500,
  NOT_IMPLEMENTED: 501,
  BAD_GATEWAY: 502,
  SERVICE_UNAVAILABLE: 503
}
Object.freeze(HttpStatus)

export function isClientError(statusCode) {
  return _.inRange(statusCode, 400, 499)
}

export function isServerError(statusCode) {
  return _.inRange(statusCode, 500, 599)
}

export default HttpStatus
