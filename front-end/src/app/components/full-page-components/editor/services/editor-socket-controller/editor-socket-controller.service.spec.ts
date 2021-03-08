import { TestBed } from '@angular/core/testing';

import { EditorSocketControllerService } from './editor-socket-controller.service';

describe('EditorSocketControllerService', () => {
  let service: EditorSocketControllerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EditorSocketControllerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
