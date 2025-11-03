import {Component, OnDestroy, OnInit} from '@angular/core';
import {AttendanceResponse} from "../../../../../services/models/attendance-response";
import {AuthService} from "../../../additionalServices/auth-service";
import {WorkerAttendanceControllerService} from "../../../../../services/services/worker-attendance-controller.service";
import {UserServiceControllerService} from "../../../../../services/services/user-service-controller.service";
import {PageResponseAttendanceResponse} from "../../../../../services/models/page-response-attendance-response";
import {catchError, of, Subscription} from "rxjs";
import {UserDataService} from "../../../../components/user-data-service/user-data-service";
declare let L: any;

@Component({
  selector: 'app-stat-for-attendence',
  templateUrl: './stat-for-attendence.component.html',
  styleUrl: './stat-for-attendence.component.scss'
})
export class StatForAttendenceComponent implements OnInit, OnDestroy{

  userName: string = '';
  companyName: string = '';
  userPhotoUrl: string = '';

  // Данные посещаемости
  attendanceList: AttendanceResponse[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';

  // Пагинация
  page: number = 0;
  size: number = 10;
  totalPages: number = 0;
  totalElements: number = 0;
  isFirst: boolean = true;
  isLast: boolean = true;

  // Modal для фотографий
  showPhotoModal: boolean = false;
  selectedAttendance: AttendanceResponse | null = null;
  photos: Array<{url: string, type: string, time: string}> = [];
  selectedDate: string = '';
  fullscreenImage: string = '';
  loadingPhotos: boolean = false;

  // Map variables
  showLocationModal: boolean = false;
  selectedLocationData: any = null;
  locationType: 'checkin' | 'checkout' = 'checkin';
  private locationMap: any = null;
  private locationMarker: any = null;
  private mapInitialized: boolean = false;
  isMobileMenuOpen = false;

  private subscriptions = new Subscription();

  constructor(
    private authService: AuthService,
    private attendanceService: WorkerAttendanceControllerService,
    private userService: UserServiceControllerService,
    public userDataService: UserDataService

  ) {}

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }
    this.loadAttendance();
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
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
  loadAttendance(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.attendanceService.getAllAttendanceForAdmin({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (response: PageResponseAttendanceResponse) => {
        this.attendanceList = response.content || [];

        // 🔍 ДОБАВЬ ЭТО ДЛЯ ДЕБАГА
        console.log('🔍 Checking manual entries:');
        this.attendanceList.forEach(att => {
          if (!att.checkInPhotoUrl || !att.checkOutPhotoUrl) {
            console.log(`ID: ${att.attendanceId}`, {
              checkInPhoto: att.checkInPhotoUrl,
              checkOutPhoto: att.checkOutPhotoUrl,
              worker: `${att.firstName} ${att.lastName}`
            });
          }
        });

        this.totalPages = response.totalPages || 0;
        this.totalElements = response.totalElement || 0;
        this.isFirst = response.first || true;
        this.isLast = response.last || true;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading attendance:', error);
        this.errorMessage = 'Failed to load attendance data. Please try again.';
        this.isLoading = false;
      }
    });
  }
// Open location modal for Check In
  openCheckInLocation(attendance: AttendanceResponse): void {
    if (!attendance.checkInLatitude || !attendance.checkInLongitude) {
      this.errorMessage = 'Check-in location not available';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    this.locationType = 'checkin';
    this.selectedLocationData = {
      latitude: attendance.checkInLatitude || 0,  // ← ДОБАВЬ || 0
      longitude: attendance.checkInLongitude || 0, // ← ДОБАВЬ || 0
      location: attendance.checkInLocation || 'Location not available',
      time: attendance.checkInTime || '',
      workerName: `${attendance.firstName || ''} ${attendance.lastName || ''}`,
      type: 'Check In'
    };
    this.showLocationModal = true;

    setTimeout(() => {
      this.initializeLocationMap();
    }, 200);
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }

// Open location modal for Check Out
  openCheckOutLocation(attendance: AttendanceResponse): void {
    if (!attendance.checkOutLatitude || !attendance.checkOutLongitude) {
      this.errorMessage = 'Check-out location not available';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    this.locationType = 'checkout';
    this.selectedLocationData = {
      latitude: attendance.checkOutLatitude || 0,  // ← ДОБАВЬ || 0
      longitude: attendance.checkOutLongitude || 0, // ← ДОБАВЬ || 0
      location: attendance.checkOutLocation || 'Location not available',
      time: attendance.checkOutTime || '',
      workerName: `${attendance.firstName || ''} ${attendance.lastName || ''}`,
      type: 'Check Out'
    };
    this.showLocationModal = true;

    setTimeout(() => {
      this.initializeLocationMap();
    }, 200);
  }
// Close location modal
  closeLocationModal(): void {
    this.showLocationModal = false;
    this.selectedLocationData = null;

    if (this.locationMap) {
      this.locationMap.remove();
      this.locationMap = null;
      this.locationMarker = null;
      this.mapInitialized = false;
    }
  }

// Initialize Leaflet map for location
  private initializeLocationMap(): void {
    if (!this.selectedLocationData) return;

    const lat = parseFloat(this.selectedLocationData.latitude);
    const lng = parseFloat(this.selectedLocationData.longitude);

    if (isNaN(lat) || isNaN(lng)) {
      console.error('Invalid coordinates');
      return;
    }

    if (typeof L === 'undefined') {
      console.error('Leaflet is not loaded!');
      this.loadLeafletFromCDN();
      return;
    }

    const mapElement = document.getElementById('attendanceLocationMap');
    if (!mapElement) {
      console.error('Map element not found');
      return;
    }

    mapElement.innerHTML = '';

    try {
      mapElement.style.width = '100%';
      mapElement.style.height = '100%';
      mapElement.style.minHeight = '400px';

      // Create map
      this.locationMap = L.map(mapElement, {
        center: [lat, lng],
        zoom: 16,
        zoomControl: true,
        attributionControl: true
      });

      // Add tiles
      L.tileLayer('https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png', {
        attribution: '© OpenStreetMap contributors © CARTO',
        subdomains: 'abcd',
        maxZoom: 20,
        minZoom: 2
      }).addTo(this.locationMap);

      // Marker color based on type
      const markerColor = this.locationType === 'checkin' ? '#10b981' : '#ef4444';
      const iconHtml = `
      <div style="
        background: ${markerColor};
        width: 30px;
        height: 30px;
        border-radius: 50% 50% 50% 0;
        transform: rotate(-45deg);
        border: 3px solid white;
        box-shadow: 0 2px 8px rgba(0,0,0,0.3);
        display: flex;
        align-items: center;
        justify-content: center;
      ">
        <div style="
          width: 10px;
          height: 10px;
          background: white;
          border-radius: 50%;
          transform: rotate(45deg);
        "></div>
      </div>
    `;

      const customIcon = L.divIcon({
        className: 'custom-div-icon',
        html: iconHtml,
        iconSize: [30, 30],
        iconAnchor: [15, 30]
      });

      // Add marker
      this.locationMarker = L.marker([lat, lng], { icon: customIcon })
        .addTo(this.locationMap);

      // Popup content
      const popupContent = `
      <div style="padding: 10px; min-width: 200px;">
        <h3 style="margin: 0 0 10px 0; color: #1f2937; font-size: 16px;">
          <i class="fas fa-user" style="color: ${markerColor};"></i>
          ${this.selectedLocationData.workerName}
        </h3>
        <p style="margin: 5px 0; color: #6b7280; font-size: 13px;">
          <i class="fas fa-clock" style="color: ${markerColor}; width: 20px;"></i>
          <strong>${this.selectedLocationData.type}:</strong> ${this.formatDateTime(this.selectedLocationData.time)}
        </p>
        <p style="margin: 5px 0; color: #6b7280; font-size: 13px;">
          <i class="fas fa-map-marker-alt" style="color: ${markerColor}; width: 20px;"></i>
          ${this.selectedLocationData.location || 'Location not available'}
        </p>
        <p style="margin: 5px 0; color: #6b7280; font-size: 11px;">
          <i class="fas fa-crosshairs" style="color: ${markerColor}; width: 20px;"></i>
          ${lat.toFixed(6)}, ${lng.toFixed(6)}
        </p>
      </div>
    `;

      this.locationMarker.bindPopup(popupContent).openPopup();

      setTimeout(() => {
        if (this.locationMap) {
          this.locationMap.invalidateSize(true);
        }
      }, 100);

      this.mapInitialized = true;
      console.log('Location map initialized successfully');

    } catch (error) {
      console.error('Error initializing location map:', error);
    }
  }

// Load Leaflet from CDN if not available
  private loadLeafletFromCDN(): void {
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
    document.head.appendChild(link);

    const script = document.createElement('script');
    script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
    script.onload = () => {
      console.log('Leaflet loaded from CDN');
      setTimeout(() => this.initializeLocationMap(), 500);
    };
    document.head.appendChild(script);
  }

// Check if location exists
  hasCheckInLocation(attendance: AttendanceResponse): boolean {
    return typeof attendance.checkInLatitude === 'number' &&
      typeof attendance.checkInLongitude === 'number';
  }

  hasCheckOutLocation(attendance: AttendanceResponse): boolean {
    return typeof attendance.checkOutLatitude === 'number' &&
      typeof attendance.checkOutLongitude === 'number';
  }



  // Методы пагинации
  goToPage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPages) {
      this.page = pageNumber;
      this.loadAttendance();
    }
  }

  nextPage(): void {
    if (!this.isLast) {
      this.page++;
      this.loadAttendance();
    }
  }

  previousPage(): void {
    if (!this.isFirst) {
      this.page--;
      this.loadAttendance();
    }
  }

  changePageSize(newSize: number): void {
    this.size = newSize;
    this.page = 0;
    this.loadAttendance();
  }

  openPhotoModal(attendance: AttendanceResponse): void {
    // Проверяем что URL не пустые строки и не null
    const hasCheckInPhoto = attendance.checkInPhotoUrl &&
      attendance.checkInPhotoUrl.trim() !== '' &&
      attendance.checkInPhotoUrl !== 'null' &&
      attendance.checkInPhotoUrl !== 'undefined';

    const hasCheckOutPhoto = attendance.checkOutPhotoUrl &&
      attendance.checkOutPhotoUrl.trim() !== '' &&
      attendance.checkOutPhotoUrl !== 'null' &&
      attendance.checkOutPhotoUrl !== 'undefined';

    if (!hasCheckInPhoto && !hasCheckOutPhoto) {
      this.errorMessage = 'No photos available for this attendance record (manual entry)';
      setTimeout(() => this.errorMessage = '', 3000);
      return;
    }

    this.selectedAttendance = attendance;
    this.showPhotoModal = true;

    if (attendance.checkInTime) {
      this.selectedDate = attendance.checkInTime.split('T')[0];
    } else {
      this.selectedDate = new Date().toISOString().split('T')[0];
    }

    this.loadPhotosForAttendance();
  }
  hasValidPhotos(attendance: AttendanceResponse): boolean {
    const invalidValues = [
      'Manual entry by admin',
      'manual-update',
      'manual-update-assumed',
      'Manual entry',
      'upload-failed',
      'uploading'
    ];

    const checkInValid = attendance.checkInPhotoUrl &&
      attendance.checkInPhotoUrl.trim() !== '' &&
      attendance.checkInPhotoUrl !== 'null' &&
      attendance.checkInPhotoUrl !== 'undefined' &&
      !invalidValues.includes(attendance.checkInPhotoUrl);

    const checkOutValid = attendance.checkOutPhotoUrl &&
      attendance.checkOutPhotoUrl.trim() !== '' &&
      attendance.checkOutPhotoUrl !== 'null' &&
      attendance.checkOutPhotoUrl !== 'undefined' &&
      !invalidValues.includes(attendance.checkOutPhotoUrl);

    return !!(checkInValid || checkOutValid);
  }

  loadPhotosForAttendance(): void {
    if (!this.selectedAttendance?.attendanceId) {
      this.loadingPhotos = false; // Сразу ставим false
      this.photos = [];
      return;
    }

    this.loadingPhotos = true;
    this.photos = [];

    this.attendanceService.getPhotosByAttendanceId({
      attendanceId: this.selectedAttendance.attendanceId
    }).pipe(
      catchError(error => {
        console.error('Error loading photos:', error);
        this.loadingPhotos = false; // Важно!
        this.photos = [];
        return of(null);
      })
    ).subscribe({
      next: (response) => {
        if (response) {
          const tempPhotos = [];

          if (response.checkInPhotoUrl) {
            tempPhotos.push({
              url: response.checkInPhotoUrl,
              type: 'punch-in',
              time: response.checkInTime || ''
            });
          }

          if (response.checkOutPhotoUrl) {
            tempPhotos.push({
              url: response.checkOutPhotoUrl,
              type: 'punch-out',
              time: response.checkOutTime || ''
            });
          }

          this.photos = tempPhotos; // Присваиваем один раз
        }

        this.loadingPhotos = false;
      },
      error: (error) => {
        console.error('Error loading photos:', error);
        this.loadingPhotos = false;
        this.photos = [];
      }
    });
  }

  // ✅ Форматирование времени из строки "HH:mm:ss"
  formatTimeOnly(timeString: string | undefined): string {
    if (!timeString) return '';

    try {
      const parts = timeString.split(':');
      if (parts.length >= 2) {
        const hours = parseInt(parts[0]);
        const minutes = parts[1];
        const ampm = hours >= 12 ? 'PM' : 'AM';
        const displayHours = hours % 12 || 12;
        return `${displayHours}:${minutes} ${ampm}`;
      }
      return timeString;
    } catch (e) {
      console.error('Error formatting time:', e);
      return timeString;
    }
  }

  openFullscreen(url: string): void {
    this.fullscreenImage = url;
  }

  closeFullscreen(): void {
    this.fullscreenImage = '';
  }

  closeModal(): void {
    this.showPhotoModal = false;
    this.selectedAttendance = null;
    this.photos = [];
  }

  onImageError(event: any): void {
    event.target.src = 'assets/images/no-photo-placeholder.png';
  }

  // Вспомогательные методы
  formatDateTime(dateTime: string | undefined): string {
    if (!dateTime) return 'N/A';
    return new Date(dateTime).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatDate(date: string | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  formatCurrency(amount: number | undefined): string {
    if (!amount) return '$0.00';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }

  formatHours(hours: number | undefined): string {
    if (!hours) return '0h';
    return `${hours.toFixed(2)}h`;
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPagesToShow = 5;
    let startPage = Math.max(0, this.page - Math.floor(maxPagesToShow / 2));
    let endPage = Math.min(this.totalPages - 1, startPage + maxPagesToShow - 1);

    if (endPage - startPage < maxPagesToShow - 1) {
      startPage = Math.max(0, endPage - maxPagesToShow + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    return pages;
  }

  preventClose(event: MouseEvent): void {
    event.stopPropagation();
  }

  Math = Math;

// Добавь этот метод в StatForAttendenceComponent
  hasPhotos(attendance: AttendanceResponse): boolean {
    return !!(
      (attendance.checkInPhotoUrl && attendance.checkInPhotoUrl.trim() !== '') ||
      (attendance.checkOutPhotoUrl && attendance.checkOutPhotoUrl.trim() !== '')
    );
  }
}
