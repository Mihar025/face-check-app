import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  NgZone,
  OnDestroy,
  OnInit
} from '@angular/core';
import { AuthService } from '../../../additionalServices/auth-service';
import {
  AdminControllerService, // оставил импорт на будущее, если понадобится
  CompanyControllerService,
  TrackLocationControllerService,
  UserServiceControllerService
} from '../../../../../services/services';
import { RelatedUserInCompanyResponse } from '../../../../../services/models/related-user-in-company-response';
import { LocationRecordDto } from '../../../../../services/models/location-record-dto';

// Leaflet global
declare let L: any;

interface CompanyLite { id: number | string; name: string; }

interface LocationData {
  latitude: number;
  longitude: number;
  timestamp: string;
  accuracy: number;
  speed: number;
  batteryLevel: number;
  distanceFromPrevious: number;
}

@Component({
  selector: 'app-location-tracking-app-owner',
  templateUrl: './location-tracking-app-owner.component.html',
  styleUrls: ['./location-tracking-app-owner.component.scss']
})
export class LocationTrackingAppOwnerComponent implements OnInit, OnDestroy, AfterViewInit {
  // Header info
  userName = '';
  userPhotoUrl = '';

  // Filters/companies
  companies: CompanyLite[] = [];
  selectedCompanyFilter: string = '';

  // Employees state (GLOBAL)
  employees: RelatedUserInCompanyResponse[] = [];
  filteredEmployees: RelatedUserInCompanyResponse[] = [];
  pagedEmployees: RelatedUserInCompanyResponse[] = [];
  employeesPage = 0;
  pageSize = 50;
  get employeesTotalPages() {
    return Math.max(1, Math.ceil(this.filteredEmployees.length / this.pageSize));
  }

  selectedEmployee: RelatedUserInCompanyResponse | null = null;
  searchTerm = '';

  // Map & locations
  locations: LocationData[] = [];
  currentLocationIndex = 0;
  private map: any = null;
  private markersLayer: any = null;
  private routePolyline: any = null;
  private currentMarker: any = null;
  private mapInitialized = false;

  // Playback
  isPlaying = false;
  playbackSpeed = 1;
  private playbackInterval: any = null;

  // UI state
  loading = false;
  loadingEmployees = false;
  errorMessage = '';
  successMessage = '';
  selectedDate: string;
  maxDate: string;

  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private companyService: CompanyControllerService,
    private adminService: AdminControllerService, // на будущее
    private locationService: TrackLocationControllerService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {
    const today = new Date();
    const y = today.getFullYear();
    const m = String(today.getMonth() + 1).padStart(2, '0');
    const d = String(today.getDate()).padStart(2, '0');
    this.selectedDate = `${y}-${m}-${d}`;
    this.maxDate = this.selectedDate;
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Lifecycle
  // ────────────────────────────────────────────────────────────────────────────────
  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    const role = this.authService.getUserRole?.();
    // Разрешенные роли для этого экрана — при необходимости дополни
    if (role !== 'AppOwner' && role !== 'ADMIN' && role !== 'SUPER_ADMIN') {
      window.location.href = '/main-page/user';
      return;
    }

    this.loadHeaderInfo();
    this.loadEmployeesGlobal();
  }

  ngAfterViewInit(): void {
    this.ngZone.runOutsideAngular(() => setTimeout(() => this.initializeMap(), 300));
  }

  ngOnDestroy(): void {
    this.stopPlayback();
    if (this.map) {
      this.map.remove();
      this.map = null;
      this.mapInitialized = false;
    }
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Header / profile
  // ────────────────────────────────────────────────────────────────────────────────
  private loadHeaderInfo(): void {
    this.userService.findWorkerFullName().subscribe({
      next: (r) => (this.userName = r?.fullName || this.userName),
      error: () => {}
    });
    this.userService.findWorkerFullContactInformation().subscribe({
      next: (r) => (this.userPhotoUrl = r?.photoUrl || this.userPhotoUrl),
      error: () => {}
    });
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Employees (GLOBAL via /company/find-all-employees)
  // ────────────────────────────────────────────────────────────────────────────────
  private loadEmployeesGlobal(): void {
    this.loadingEmployees = true;
    this.errorMessage = '';
    this.successMessage = '';

    const pageSizeBackend = 500; // подстрой под лимиты бэка
    const acc: RelatedUserInCompanyResponse[] = [];

    const fetchPage = (page: number) => {
      this.companyService.findAllEmployees({ page, size: pageSizeBackend }).subscribe({
        next: (res: any) => {
          const content: RelatedUserInCompanyResponse[] = res?.content || res?.items || [];
          acc.push(...content);

          // Определяем общий объем/страницы в разных форматах
          const totalElements =
            res?.totalElements ??
            res?.totalElement ??
            res?.total_items ??
            (typeof res?.total === 'number' ? res.total : undefined);

          const totalPages =
            res?.totalPages ??
            res?.total_pages ??
            (typeof totalElements === 'number'
              ? Math.ceil(totalElements / pageSizeBackend)
              : undefined);

          const hasMoreByPages =
            typeof totalPages === 'number' ? page + 1 < totalPages : false;

          const hasMoreByContent = content.length === pageSizeBackend;

          if (hasMoreByPages || hasMoreByContent) {
            fetchPage(page + 1);
            return;
          }

          // Готово
          this.employees = acc;

          // Список компаний для фильтра (по companyName)
          this.companies = Array.from(
            new Map(
              this.employees
                .filter(e => !!e.companyName)
                .map(e => [String(e.companyName), { id: String(e.companyName), name: String(e.companyName) }])
            ).values()
          );

          this.applyFiltersAndPaging();
          this.loadingEmployees = false;

          this.successMessage = `Loaded ${this.employees.length} employees`;
          setTimeout(() => (this.successMessage = ''), 4000);
        },
        error: (err) => {
          this.errorMessage = `Failed to load employees: ${err?.message || 'Unknown error'}`;
          this.loadingEmployees = false;
        }
      });
    };

    fetchPage(0);
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Filtering & paging
  // ────────────────────────────────────────────────────────────────────────────────
  filterEmployees(): void {
    const q = (this.searchTerm || '').trim().toLowerCase();
    const selectedCompanyName = this.companyNameById(this.selectedCompanyFilter) || '';

    this.filteredEmployees = this.employees.filter((e) => {
      const matchesQ =
        !q ||
        (e.firstName || '').toLowerCase().includes(q) ||
        (e.lastName || '').toLowerCase().includes(q) ||
        (e.email || '').toLowerCase().includes(q) ||
        String(e.workerId || '').includes(q) ||
        String(e.companyName || '').toLowerCase().includes(q);

      const matchesCompany =
        !this.selectedCompanyFilter ||
        (e.companyName && e.companyName === selectedCompanyName);

      return matchesQ && matchesCompany;
    });

    this.employeesPage = 0;
    this.slicePaging();
  }

  private companyNameById(id: string): string | undefined {
    // здесь id == name (мы так сформировали companies)
    const c = this.companies.find((x) => String(x.id) === String(id));
    return c?.name;
  }

  onPageSizeChange(): void {
    this.employeesPage = 0;
    this.slicePaging();
  }

  private slicePaging(): void {
    const start = this.employeesPage * this.pageSize;
    const end = start + this.pageSize;
    this.pagedEmployees = this.filteredEmployees.slice(start, end);
  }

  private applyFiltersAndPaging(): void {
    this.filteredEmployees = [...this.employees];
    this.slicePaging();
  }

  nextEmployeesPage(): void {
    if (this.employeesPage < this.employeesTotalPages - 1) {
      this.employeesPage++;
      this.slicePaging();
    }
  }

  prevEmployeesPage(): void {
    if (this.employeesPage > 0) {
      this.employeesPage--;
      this.slicePaging();
    }
  }

  trackByEmployee = (_: number, item: RelatedUserInCompanyResponse) =>
    item.workerId ?? item.email ?? item.firstName;

  // ────────────────────────────────────────────────────────────────────────────────
  // Map
  // ────────────────────────────────────────────────────────────────────────────────
  private initializeMap(): void {
    if (typeof L === 'undefined') {
      this.loadLeafletFromCDN();
      return;
    }

    const mapElement = document.getElementById('locationMap');
    if (!mapElement) {
      setTimeout(() => this.initializeMap(), 400);
      return;
    }

    if (this.map) this.map.remove();
    mapElement.innerHTML = '';

    try {
      this.map = L.map(mapElement, {
        center: [40.7128, -74.0060],
        zoom: 12,
        zoomControl: true,
        attributionControl: true
      });

      L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        attribution: '© OpenStreetMap contributors © CARTO',
        subdomains: 'abcd',
        maxZoom: 19,
        minZoom: 2
      }).addTo(this.map);

      this.markersLayer = L.layerGroup().addTo(this.map);

      setTimeout(() => this.map?.invalidateSize(true), 150);
      window.addEventListener('resize', () =>
        setTimeout(() => this.map?.invalidateSize(true), 150)
      );

      this.mapInitialized = true;
      this.ngZone.run(() => this.cdr.detectChanges());
    } catch (e) {
      console.error('Map init error', e);
      this.mapInitialized = false;
    }
  }

  private loadLeafletFromCDN(): void {
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
    document.head.appendChild(link);

    const script = document.createElement('script');
    script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
    script.onload = () => setTimeout(() => this.initializeMap(), 300);
    document.head.appendChild(script);
  }

  private clearMap(): void {
    if (this.markersLayer) this.markersLayer.clearLayers();
    this.routePolyline = null;
    this.currentMarker = null;
  }

  private drawRoute(): void {
    if (!this.map || this.locations.length === 0 || typeof L === 'undefined') return;

    this.clearMap();

    const latLngs = this.locations.map((l) => L.latLng(l.latitude, l.longitude));
    this.routePolyline = L.polyline(latLngs, {
      color: '#5B47E0',
      weight: 4,
      opacity: 0.7
    }).addTo(this.markersLayer);

    const start = this.locations[0];
    const end = this.locations[this.locations.length - 1];

    const makeIcon = (bg: string, text: string) =>
      L.divIcon({
        className: 'custom-div-icon',
        html: `<div style="background:${bg};color:#fff;border-radius:50%;width:30px;height:30px;display:flex;align-items:center;justify-content:center;font-weight:700;border:2px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,.3)">${text}</div>`,
        iconSize: [30, 30],
        iconAnchor: [15, 15]
      });

    L.marker([start.latitude, start.longitude], { icon: makeIcon('#00D97E', 'S') })
      .bindPopup(`<b>Start</b><br>${this.formatDateTime(start.timestamp)}`)
      .addTo(this.markersLayer);

    if (end) {
      L.marker([end.latitude, end.longitude], { icon: makeIcon('#FF5757', 'E') })
        .bindPopup(`<b>End</b><br>${this.formatDateTime(end.timestamp)}`)
        .addTo(this.markersLayer);
    }

    this.updateCurrentMarker();

    const bounds = L.latLngBounds(latLngs);
    this.map.fitBounds(bounds, { padding: [50, 50], maxZoom: 16 });
  }

  private updateCurrentMarker(): void {
    if (!this.map || !this.locations[this.currentLocationIndex] || typeof L === 'undefined') return;
    if (this.currentMarker) this.currentMarker.remove();

    const p = this.locations[this.currentLocationIndex];
    const icon = L.divIcon({
      className: 'custom-div-icon',
      html: '<div style="background:#5B47E0;width:20px;height:20px;border-radius:50%;border:3px solid #fff;box-shadow:0 2px 8px rgba(0,0,0,.3)"></div>',
      iconSize: [20, 20],
      iconAnchor: [10, 10]
    });

    this.currentMarker = L.marker([p.latitude, p.longitude], { icon }).addTo(this.markersLayer);
    if (this.isPlaying) this.map.panTo([p.latitude, p.longitude]);
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Actions
  // ────────────────────────────────────────────────────────────────────────────────
  selectEmployee(e: RelatedUserInCompanyResponse): void {
    this.selectedEmployee = e;
    this.locations = [];
    this.currentLocationIndex = 0;
    this.stopPlayback();

    if (!this.mapInitialized) {
      this.ngZone.runOutsideAngular(() => this.initializeMap());
    } else {
      setTimeout(() => this.map?.invalidateSize(true), 150);
    }

    if (e.workerId) this.loadLocationHistory();
  }

  loadLocationHistory(): void {
    if (!this.selectedEmployee?.workerId || !this.selectedDate) return;

    this.loading = true;
    this.errorMessage = '';
    this.locations = [];
    this.clearMap();

    this.locationService.getLocationHistory({
      userId: this.selectedEmployee.workerId,
      startDate: this.selectedDate
    }).subscribe({
      next: (res: LocationRecordDto[]) => {
        if (!Array.isArray(res)) {
          this.errorMessage = 'Invalid response format';
          this.loading = false;
          return;
        }

        this.locations = res
          .filter((x) => x.latitude && x.longitude && x.latitude !== 0 && x.longitude !== 0)
          .map((x) => ({
            latitude: x.latitude!,
            longitude: x.longitude!,
            timestamp: x.timestamp || new Date().toISOString(),
            accuracy: x.accuracy || 10,
            speed: x.speed || 0,
            batteryLevel: x.batteryLevel || 0,
            distanceFromPrevious: x.distanceFromPrevious || 0
          }))
          .sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());

        this.loading = false;

        if (this.locations.length) {
          this.currentLocationIndex = 0;
          if (!this.mapInitialized || !this.map) {
            this.ngZone.runOutsideAngular(() =>
              setTimeout(() => {
                this.initializeMap();
                setTimeout(() => this.drawRoute(), 300);
              }, 200)
            );
          } else {
            this.drawRoute();
          }

          const start = this.formatTime(this.locations[0].timestamp);
          const end = this.formatTime(this.locations[this.locations.length - 1].timestamp);
          this.successMessage = `Loaded ${this.locations.length} location points (${start} - ${end})`;
          setTimeout(() => (this.successMessage = ''), 4000);
        } else {
          this.errorMessage = 'No location data found for selected date';
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = `Failed to load location data: ${err?.message || 'Unknown error'}`;
      }
    });
  }

  togglePlayback(): void {
    this.isPlaying ? this.stopPlayback() : this.startPlayback();
  }

  startPlayback(): void {
    if (this.locations.length < 2) return;
    this.isPlaying = true;
    this.playbackInterval = setInterval(() => {
      if (this.currentLocationIndex < this.locations.length - 1) {
        this.currentLocationIndex++;
        this.updateCurrentMarker();
      } else {
        this.stopPlayback();
      }
    }, 1000 / this.playbackSpeed);
  }

  stopPlayback(): void {
    this.isPlaying = false;
    if (this.playbackInterval) {
      clearInterval(this.playbackInterval);
      this.playbackInterval = null;
    }
  }

  onTimelineChange(event: any): void {
    const idx = parseInt(event.target.value, 10);
    this.currentLocationIndex = isNaN(idx) ? 0 : idx;
    this.updateCurrentMarker();
  }

  refreshData(): void {
    if (this.selectedEmployee) this.loadLocationHistory();
    this.loadEmployeesGlobal();
    if (!this.mapInitialized || !this.map) this.initializeMap();
    else setTimeout(() => this.map?.invalidateSize(true), 150);
  }

  // ────────────────────────────────────────────────────────────────────────────────
  // Formatting utils
  // ────────────────────────────────────────────────────────────────────────────────
  get currentLocation(): LocationData | null {
    return this.locations[this.currentLocationIndex] || null;
  }

  getTimeRange(): string {
    if (!this.locations.length) return '';
    return `${this.getStartTime()} - ${this.getEndTime()}`;
  }

  getStartTime(): string {
    return this.locations.length ? this.formatTime(this.locations[0].timestamp) : '';
  }

  getEndTime(): string {
    return this.locations.length ? this.formatTime(this.locations[this.locations.length - 1].timestamp) : '';
  }

  getTotalDistance(): string {
    if (this.locations.length < 2) return '0';
    let total = 0;
    for (const l of this.locations) total += l.distanceFromPrevious || 0;
    return (total / 1000).toFixed(2);
  }

  formatDateTime(ts: string): string {
    const d = new Date(ts);
    return d.toLocaleString('en-US', {
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      timeZone: 'UTC'
    });
  }

  formatTime(ts: string): string {
    const d = new Date(ts);
    return d.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      timeZone: 'UTC'
    });
  }

  handleImageError(event: any): void {
    event.target.style.display = 'none';
    const placeholder = event.target.parentElement?.querySelector('.avatar-placeholder, .overlay-avatar-placeholder');
    if (placeholder) placeholder.style.display = 'grid';
  }
}
