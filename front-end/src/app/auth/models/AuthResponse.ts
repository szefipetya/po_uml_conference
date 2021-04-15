import { User_PublicDto } from "./User_PublicDto";
export class AuthResponse {
  user: User_PublicDto;
  jwt_token: string; success: boolean;
}
