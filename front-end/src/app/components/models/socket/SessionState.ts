import { LOCK_TYPE } from './LOCK_TYPE';

export class SessionState {
  locks: LOCK_TYPE[];
  lockerUser_id: string;
  constructor() {
    this.locks = [];
  }
  extra: any;
  has_lock(type: LOCK_TYPE) {
    return (
      this.locks.findIndex((l) => {
        l == type;
      }) > -1
    );
  }
}
