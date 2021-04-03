import { DynamicSerialObject } from '../../common/DynamicSerialObject';
import { SessionState } from '../SessionState';
import { CallbackItem } from './CallbackItem';
import { SessionInteractiveItem } from './SessionInteractiveItem';
export interface SessionInteractiveContainer extends SessionInteractiveItem {
  sessionState: SessionState;
  callback_queue: CallbackItem[];
  // updateState(state: SessionState, action_id: string): void;
  createItem(model: DynamicSerialObject, extra?: any);
  hasItem(target_id: string);
  restoreItem(item_id: string, model: DynamicSerialObject);

  deleteItem(item_id: string);
  msgPopup(msg: string);
  getId(): string;
}
