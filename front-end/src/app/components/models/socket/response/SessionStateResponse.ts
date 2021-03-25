import { SessionState } from '../SessionState';
import { CustomResponse } from './CustomResponse';
import { RESPONSE_SCOPE } from './RESPONSE_SCOPE';

export class SessionStateResponse extends CustomResponse {
  sessionState: SessionState;
  action_id: string;
}
