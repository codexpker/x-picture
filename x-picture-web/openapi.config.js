import {generateService} from '@umijs/openapi'

generateService({
  requestLibPath: "import request from '@/request'",
  schemaPath: 'http://localhost:8436/api/v3/api-docs',
  serversPath: './src',
})
