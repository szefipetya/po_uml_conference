import { MSG_TYPE } from 'src/app/components/full-page-components/editor/services/common/common.service';
import { SessionState } from '../SessionState';
import { CallbackItem } from './CallbackItem';

export interface SessionInteractiveItem {
  //private sessionState: SessionState;
  callback_queue: CallbackItem[];
  updateState(state: SessionState, m?: any): void;
  editBegin();
  editEnd();
  updateModel(model: any, action_id: string, msg?: string);
  restoreModel(model: any, action_id: string, msg?: string);
  deleteSelfFromParent();
  log(msg: string, type: MSG_TYPE);
  getId();
  updateColorOnly();
}
