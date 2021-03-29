import { TARGET_TYPE } from '../../../../models/socket/response/TARGET_TYPE';
export enum TOKEN_TYPE {
  SESSION_STATE,
  COMBINED,
}
export class InjectionToken_c {
  data: any;
  type: TOKEN_TYPE;
  target_id: string;
  target_type: TARGET_TYPE;
}
