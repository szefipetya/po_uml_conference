import { SessionState } from '../SessionState';
import { CallbackItem } from './CallbackItem';

export interface SessionInteractiveItem {
  sessionState: SessionState;
  callback_queue: CallbackItem[];
  updateState(state: SessionState, m?: any): void;
  editBegin();
  editEnd();
  updateModel(model: any, action_id: string, msg?: string);
  restoreModel(model: any, action_id: string, msg?: string);
  msgPopup(msg: string);
}
