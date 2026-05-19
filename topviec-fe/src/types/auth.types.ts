// src/types/auth.types.ts

export interface ReqLoginDTO {
  email: string
  password: string
}

export interface ReqRegisterCandidateDTO {
  email: string
  password: string
}

export interface ReqRegisterEmployerDTO {
  email: string
  password: string
  companyName: string
  companySlug: string
}

export interface UserInfo {
  id: string | number
  email: string
  role: 'CANDIDATE' | 'EMPLOYER' | 'ADMIN'
  emailVerified?: boolean
}

export interface ResLoginDTO {
  accessToken: string
  user: UserInfo
}

export interface ReqForgotPasswordDTO {
  email: string
}

export interface ReqResetPasswordDTO {
  token: string
  newPassword: string
}

export interface ReqChangePasswordDTO {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}