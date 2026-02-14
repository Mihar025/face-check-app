import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotesForPunchResponse } from '../../../../../services/models/notes-for-punch-response';
import { NotesFromPunchesControllerService } from '../../../../../services/services/notes-from-punches-controller.service';
import { AuthService } from '../../../additionalServices/auth-service';
import { UserDataService } from '../../../../components/user-data-service/user-data-service';

@Component({
  selector: 'app-notes-for-punch-admin',
  templateUrl: './notes-for-punch-admin.component.html',
  styleUrls: ['./notes-for-punch-admin.component.scss']
})
export class NotesForPunchAdminComponent implements OnInit, OnDestroy {

  // Data
  notes: NotesForPunchResponse[] = [];
  loading = false;
  errorMessage = '';

  // Pagination
  page = 0;
  size = 10;
  totalPages = 0;
  totalElements = 0;

  // Sidebar
  userName = '';
  companyName = '';
  userPhotoUrl = '';

  // Expanded notes tracking
  expandedNotes: Set<number> = new Set();

  // Character limit before "Show more"
  readonly NOTE_PREVIEW_LENGTH = 200;

  private subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private notesService: NotesFromPunchesControllerService,
    public userDataService: UserDataService
  ) {}

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    const userRole = this.authService.getUserRole();
    if (userRole !== 'ADMIN' && userRole !== 'AppOwner') {
      let targetUrl = '/';
      if (userRole === 'USER') {
        targetUrl = '/main-page/user';
      }
      window.location.href = targetUrl;
      return;
    }

    this.subscriptions.add(
      this.userDataService.userName$.subscribe(name => {
        this.userName = name;
      })
    );

    this.subscriptions.add(
      this.userDataService.companyName$.subscribe(name => {
        this.companyName = name;
      })
    );

    this.subscriptions.add(
      this.userDataService.userPhoto$.subscribe(photo => {
        this.userPhotoUrl = photo;
      })
    );

    this.loadNotes();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  loadNotes(): void {
    this.loading = true;
    this.errorMessage = '';

    this.notesService.getAllNotes({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (res) => {
        this.notes = res.content || [];
        this.totalPages = res.totalPages || 0;
        this.totalElements = res.totalElement || 0;
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load notes. Please try again.';
        this.loading = false;
        console.error('Error loading notes:', err);
      }
    });
  }

  // Pagination
  changePage(newPage: number): void {
    if (newPage < 0 || newPage >= this.totalPages) return;
    this.page = newPage;
    this.expandedNotes.clear();
    this.loadNotes();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // Format datetime
  formatDateTime(dateStr?: string): string {
    if (!dateStr) return '—';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    }) + ' at ' + date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  }

  // Format date only (for card header)
  formatDate(dateStr?: string): string {
    if (!dateStr) return '—';
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  }

  // Format time only
  formatTime(dateStr?: string): string {
    if (!dateStr) return '—';
    const date = new Date(dateStr);
    return date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    });
  }

  // Check if note needs truncation
  isLongNote(note?: string): boolean {
    return !!note && note.length > this.NOTE_PREVIEW_LENGTH;
  }

  // Get preview or full text
  getNoteText(note: string | undefined, attendanceId: number | undefined, type: 'in' | 'out'): string {
    if (!note) return '';
    const key = this.getExpandKey(attendanceId, type);
    if (this.expandedNotes.has(key) || note.length <= this.NOTE_PREVIEW_LENGTH) {
      return note;
    }
    return note.substring(0, this.NOTE_PREVIEW_LENGTH) + '...';
  }

  // Toggle expand
  toggleExpand(attendanceId: number | undefined, type: 'in' | 'out'): void {
    const key = this.getExpandKey(attendanceId, type);
    if (this.expandedNotes.has(key)) {
      this.expandedNotes.delete(key);
    } else {
      this.expandedNotes.add(key);
    }
  }

  isExpanded(attendanceId: number | undefined, type: 'in' | 'out'): boolean {
    return this.expandedNotes.has(this.getExpandKey(attendanceId, type));
  }

  private getExpandKey(attendanceId: number | undefined, type: 'in' | 'out'): number {
    return (attendanceId || 0) * 10 + (type === 'in' ? 1 : 2);
  }

  // Avatar fallback
  getInitials(name?: string): string {
    if (!name) return '?';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
  }

  hasAvatar(url?: string): boolean {
    return !!url && url.length > 0;
  }

  // Check if note has any content
  hasNotes(note: NotesForPunchResponse): boolean {
    return (!!note.notesForPunchIn && note.notesForPunchIn.trim().length > 0)
      || (!!note.notesForPunchOut && note.notesForPunchOut.trim().length > 0);
  }

  // Pagination pages array
  getVisiblePages(): number[] {
    const pages: number[] = [];
    const start = Math.max(0, this.page - 2);
    const end = Math.min(this.totalPages - 1, this.page + 2);
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    return pages;
  }
}
