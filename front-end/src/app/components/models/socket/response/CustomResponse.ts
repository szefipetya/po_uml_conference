import { RESPONSE_SCOPE } from './RESPONSE_SCOPE';
import { TARGET_TYPE } from './TARGET_TYPE';
export class CustomResponse {
  scope: RESPONSE_SCOPE;
  target_user_id: string;
  response_msg: string;
  target_id: string;
  target_type: TARGET_TYPE;
}
