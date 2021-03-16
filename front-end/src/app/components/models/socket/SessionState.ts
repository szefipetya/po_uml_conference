import { LOCK_TYPE } from './LOCK_TYPE';

export class SessionState {
  locks: LOCK_TYPE[];
  lockerUser_id: string;
}
