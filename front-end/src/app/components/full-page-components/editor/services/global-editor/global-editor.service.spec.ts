import { TestBed } from '@angular/core/testing';

import { GlobalEditorService } from './global-editor.service';

describe('GlobalEditorService', () => {
  let service: GlobalEditorService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GlobalEditorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
