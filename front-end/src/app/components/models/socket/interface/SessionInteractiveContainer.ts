import { DynamicSerialObject } from '../../common/DynamicSerialObject';
import { SessionState } from '../SessionState';
import { CallbackItem } from './CallbackItem';
export interface SessionInteractiveContainer {
  sessionState: SessionState;
  callback_queue: CallbackItem[];
  updateState(state: SessionState, action_id: string): void;

  updateItemWithOld(old_id: string, model: any);

  createItem(model: DynamicSerialObject, extra?: any);
  hasItem(target_id: string);
  restoreItem(item_id: string, model: DynamicSerialObject);

  deleteItem(item_id: string);
  msgPopup(msg: string);
  getId(): string;
}
