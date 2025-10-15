import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../../additionalServices/auth-service";
import { UserServiceControllerService } from "../../../../../services/services/user-service-controller.service";
import { AdminControllerService } from "../../../../../services/services/admin-controller.service";
import { PaymentHistoryIrsControllerService } from "../../../../../services/services/payment-history-irs-controller.service";
import { ReportAwsControllerService } from "../../../../../services/services/report-aws-controller.service";
import { TaxesControllerService } from "../../../../../services/services/taxes-controller.service";
import { PaymentHistoryRequest } from "../../../../../services/models/payment-history-request";
import { PaymentHistoryResponse } from "../../../../../services/models/payment-history-response";
import { ReportFileDto } from "../../../../../services/models/report-file-dto";
import Chart from 'chart.js/auto';
import { HttpClient } from "@angular/common/http";

@Component({
  selector: 'app-finance-page',
  templateUrl: './finance-page.component.html',
  styleUrl: './finance-page.component.scss'
})
export class FinancePageComponent implements OnInit {
  // Properties for sidebar
  userName: string = '';
  companyName: string = '';
  userPhotoUrl: string = '';
  companyId: number = 0;

  // Financial data
  companyBudget: number = 0;
  totalExpenses: number = 0;
  salaryCost: number = 0;
  currentCapital: number = 0;

  // IRS Payments
  irsPayments: PaymentHistoryResponse[] = [];
  currentPage: number = 0;
  totalPages: number = 0;
  pageSize: number = 10;

  // Reports
  reports: ReportFileDto[] = [];
  loadingReports: boolean = false;
  selectedReportType: 'all' | 'hours' | 'payroll' = 'all';

  reportsPage: number = 0;
  reportsSize: number = 5;  // Количество отчетов на странице
  reportsTotalPages: number = 0;
  paginatedReports: ReportFileDto[] = [];

  // Report Generator
  showReportGeneratorModal: boolean = false;
  selectedReportGenType: 'hours' | 'payroll' | null = null;
  reportStartDate: string = '';
  reportEndDate: string = '';
  generatingReport: boolean = false;
  today: string = new Date().toISOString().split('T')[0];

  // Success/Error Messages
  showSuccessMessage: boolean = false;
  successMessage: string = '';
  showErrorMessage: boolean = false;
  errorMessage: string = '';

  // Modals
  showBudgetModal: boolean = false;
  showPaymentModal: boolean = false;
  newBudget: number = 0;
  newPayment: PaymentHistoryRequest = {
    amount: 0,
    notes: '',
    paymentDate: '',
    paymentType: 'PAYROLL_TAX_941' as any,
    quarter: 1,
    year: new Date().getFullYear()
  };

  // Charts
  overviewChart: Chart | null = null;
  distributionChart: Chart | null = null;

  // File management
  openFolders: { [key: string]: boolean } = {
    tax: false,
    payroll: false,
    reports: false,
    invoices: false,
    bank: false
  };

  focusedChart: string | null = null;

  constructor(
    private authService: AuthService,
    private userService: UserServiceControllerService,
    private adminService: AdminControllerService,
    private paymentService: PaymentHistoryIrsControllerService,
    private reportService: ReportAwsControllerService,
    private taxesService: TaxesControllerService,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    if (!this.authService.isUserAuthenticated()) {
      this.authService.logout();
      return;
    }

    this.loadUserData();
    this.loadFinancialData();

    setTimeout(() => {
      this.initializeCharts();
    }, 500);
  }

  private loadUserData(): void {
    this.loadUserFullName();
    this.loadCompanyInfo();
    this.getUserPhoto();
  }

  private loadUserFullName(): void {
    this.userService.findWorkerFullName().subscribe(
      response => {
        if (response && response.fullName) {
          this.userName = response.fullName;
        }
      },
      error => {
        console.error('Error loading user full name:', error);
      }
    );
  }

  updatePaginatedReports(): void {
    const filtered = this.getFilteredReports();
    this.reportsTotalPages = Math.ceil(filtered.length / this.reportsSize);

    const startIndex = this.reportsPage * this.reportsSize;
    const endIndex = startIndex + this.reportsSize;
    this.paginatedReports = filtered.slice(startIndex, endIndex);
  }

  changeReportsPage(newPage: number): void {
    this.reportsPage = newPage;
    this.updatePaginatedReports();
  }

  private loadCompanyInfo(): void {
    this.userService.findWorkerCompanyName().subscribe(
      response => {
        if (response && response.companyName) {
          this.companyName = response.companyName;

          this.userService.findWorkerCompanyIdByAuthentication().subscribe(
            idResponse => {
              if (idResponse && idResponse.companyId) {
                this.companyId = idResponse.companyId;
                console.log('Company loaded:', this.companyName, 'ID:', this.companyId);

                this.loadIRSPayments();
                this.loadCompanyReports();
              }
            },
            error => {
              console.error('Error loading company ID:', error);
            }
          );
        }
      },
      error => {
        console.error('Error loading company name:', error);
      }
    );
  }

  private getUserPhoto(): void {
    this.userService.findWorkerFullContactInformation().subscribe(
      response => {
        if (response && response.photoUrl) {
          this.userPhotoUrl = response.photoUrl;
        }
      },
      error => {
        console.error('Error loading user photo:', error);
      }
    );
  }

  private loadFinancialData(): void {
    this.adminService.getBudget().subscribe(
      budget => {
        this.companyBudget = budget || 0;
        this.updateCharts();
      },
      error => console.error('Error loading budget:', error)
    );

    this.adminService.getExpenses().subscribe(
      expenses => {
        this.totalExpenses = expenses || 0;
        this.updateCharts();
      },
      error => console.error('Error loading expenses:', error)
    );

    this.adminService.getSalariesCost().subscribe(
      salaries => {
        this.salaryCost = salaries || 0;
        this.updateCharts();
      },
      error => console.error('Error loading salary costs:', error)
    );

    this.adminService.getProfit().subscribe(
      profit => {
        this.currentCapital = profit || 0;
        this.updateCharts();
      },
      error => console.error('Error loading current capital:', error)
    );
  }

  loadIRSPayments(): void {
    this.paymentService.getAllPayments({
      companyId: this.companyId,
      page: this.currentPage,
      size: this.pageSize
    }).subscribe(
      response => {
        if (response) {
          this.irsPayments = response.content || [];
          this.totalPages = response.totalPages || 0;
        }
      },
      error => {
        console.error('Error loading IRS payments:', error);
        this.irsPayments = [];
      }
    );
  }
  loadCompanyReports(): void {
    if (!this.companyId || !this.companyName) {
      console.log('Waiting for company data...');
      return;
    }

    this.loadingReports = true;
    this.reportService.getCompanyReports({
      companyId: this.companyId,
      companyName: this.companyName
    }).subscribe(
      reports => {
        this.reports = reports || [];
        this.reportsPage = 0;  // ДОБАВЛЕНО: сброс на первую страницу
        this.updatePaginatedReports();  // ДОБАВЛЕНО
        this.loadingReports = false;
        console.log('Reports loaded:', this.reports.length);
      },
      error => {
        console.error('Error loading reports:', error);
        this.loadingReports = false;
        this.reports = [];
        this.paginatedReports = [];  // ДОБАВЛЕНО
      }
    );
  }

  onReportTypeChange(): void {
    this.reportsPage = 0;  // Сброс на первую страницу
    this.updatePaginatedReports();
  }

  // Report Generator Methods
  openReportGeneratorModal(): void {
    this.showReportGeneratorModal = true;
    this.selectedReportGenType = null;
    this.reportStartDate = '';
    this.reportEndDate = '';
  }

  closeReportGeneratorModal(): void {
    this.showReportGeneratorModal = false;
    this.selectedReportGenType = null;
    this.reportStartDate = '';
    this.reportEndDate = '';
  }

  selectReportType(type: 'hours' | 'payroll'): void {
    this.selectedReportGenType = type;
  }

  setDatePreset(preset: string): void {
    const now = new Date();
    let startDate = new Date();
    let endDate = new Date();

    switch (preset) {
      case 'thisMonth':
        startDate = new Date(now.getFullYear(), now.getMonth(), 1);
        endDate = new Date(now.getFullYear(), now.getMonth() + 1, 0);
        break;
      case 'lastMonth':
        startDate = new Date(now.getFullYear(), now.getMonth() - 1, 1);
        endDate = new Date(now.getFullYear(), now.getMonth(), 0);
        break;
      case 'thisQuarter':
        const quarter = Math.floor(now.getMonth() / 3);
        startDate = new Date(now.getFullYear(), quarter * 3, 1);
        endDate = new Date(now.getFullYear(), quarter * 3 + 3, 0);
        break;
      case 'lastQuarter':
        const lastQuarter = Math.floor(now.getMonth() / 3) - 1;
        const year = lastQuarter < 0 ? now.getFullYear() - 1 : now.getFullYear();
        const q = lastQuarter < 0 ? 3 : lastQuarter;
        startDate = new Date(year, q * 3, 1);
        endDate = new Date(year, q * 3 + 3, 0);
        break;
      case 'thisYear':
        startDate = new Date(now.getFullYear(), 0, 1);
        endDate = new Date(now.getFullYear(), 11, 31);
        break;
    }

    this.reportStartDate = startDate.toISOString().split('T')[0];
    this.reportEndDate = endDate.toISOString().split('T')[0];
  }

  formatDateRange(startDate: string, endDate: string): string {
    if (!startDate || !endDate) return '';

    const start = new Date(startDate);
    const end = new Date(endDate);

    const options: Intl.DateTimeFormatOptions = {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    };

    return `${start.toLocaleDateString('en-US', options)} - ${end.toLocaleDateString('en-US', options)}`;
  }
// В начале метода generateReport(), замените весь код на:
  generateReport(): void {
    if (!this.selectedReportGenType || !this.reportStartDate || !this.reportEndDate || !this.companyId) {
      console.error('Missing required data for report generation');
      this.showError('Please select report type and date range');
      return;
    }

    this.generatingReport = true;
    const reportTypeName = this.selectedReportGenType === 'hours' ? 'Hours Report' : 'Payroll Summary Report';

    const baseUrl = 'http://localhost:8088/api/v1';
    let url = '';

    if (this.selectedReportGenType === 'hours') {
      url = `${baseUrl}/taxes-forms/summary/hoursReport/${this.companyId}?startDate=${this.reportStartDate}&endDate=${this.reportEndDate}`;
    } else {
      url = `${baseUrl}/taxes-forms/summary/${this.companyId}?startDate=${this.reportStartDate}&endDate=${this.reportEndDate}`;
    }

    // Используем HttpClient с правильными заголовками
    const token = localStorage.getItem('authToken'); // или где вы храните токен

    this.http.get(url, {
      responseType: 'blob',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }).subscribe(
      (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `${this.selectedReportGenType === 'hours' ? 'Hours_Report' : 'Payroll_Summary'}_${this.reportStartDate}_to_${this.reportEndDate}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);

        this.showSuccess(`${reportTypeName} generated successfully!`);
        this.generatingReport = false;
        this.closeReportGeneratorModal();

        setTimeout(() => {
          this.loadCompanyReports();
        }, 1500);
      },
      error => {
        console.error('Error generating report:', error);
        this.showError(`Failed to generate ${reportTypeName}. Please try again.`);
        this.generatingReport = false;
      }
    );
  }

  // Success/Error Message Methods
  showSuccess(message: string): void {
    this.successMessage = message;
    this.showSuccessMessage = true;

    setTimeout(() => {
      this.showSuccessMessage = false;
      this.successMessage = '';
    }, 5000);
  }

  showError(message: string): void {
    this.errorMessage = message;
    this.showErrorMessage = true;

    setTimeout(() => {
      this.showErrorMessage = false;
      this.errorMessage = '';
    }, 5000);
  }

  // Other report methods
  getFilteredReports(): ReportFileDto[] {
    if (this.selectedReportType === 'hours') {
      return this.reports.filter(r => r.reportType === 'Hours Report');
    } else if (this.selectedReportType === 'payroll') {
      return this.reports.filter(r => r.reportType === 'Payroll Report');
    }
    return this.reports;
  }

  downloadReport(report: ReportFileDto): void {
    const baseUrl = 'http://localhost:8088/api/v1';
    window.open(`${baseUrl}/aws-reports/download?key=${encodeURIComponent(report.key || '')}`, '_blank');
  }

  viewReport(report: ReportFileDto): void {
    const baseUrl = 'http://localhost:8088/api/v1';
    window.open(`${baseUrl}/aws-reports/view?key=${encodeURIComponent(report.key || '')}`, '_blank');
  }

  deleteReport(report: ReportFileDto): void {
    if (confirm(`Are you sure you want to delete ${report.fileName}?`)) {
      this.reportService.deleteReport({
        key: report.key!
      }).subscribe(
        () => {
          this.showSuccess('Report deleted successfully!');
          this.loadCompanyReports();
        },
        error => {
          console.error('Error deleting report:', error);
          this.showError('Failed to delete report');
        }
      );
    }
  }

  formatFileSize(bytes: number | undefined): string {
    if (!bytes) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  formatReportDate(dateStr: string | undefined): string {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleDateString();
  }

  getReportTypeClass(type: string | undefined): string {
    if (type === 'Hours Report') return 'hours';
    if (type === 'Payroll Report') return 'payroll';
    return 'default';
  }

  // Budget Modal Methods
  openBudgetModal(): void {
    this.newBudget = this.companyBudget;
    this.showBudgetModal = true;
  }

  closeBudgetModal(): void {
    this.showBudgetModal = false;
  }

  saveBudget(): void {
    if (this.newBudget && this.newBudget > 0) {
      this.adminService.setBudget({
        body: this.newBudget
      }).subscribe(
        response => {
          this.companyBudget = this.newBudget;
          this.loadFinancialData();
          this.closeBudgetModal();
          this.showSuccess('Budget saved successfully!');
        },
        error => {
          console.error('Error saving budget:', error);
          this.showError('Failed to save budget. Please try again.');
        }
      );
    }
  }

  // Payment Modal Methods
  openPaymentModal(): void {
    const currentDate = new Date();
    this.newPayment = {
      amount: 0,
      notes: '',
      paymentDate: currentDate.toISOString().split('T')[0],
      paymentType: 'PAYROLL_TAX_941' as any,
      quarter: Math.floor((currentDate.getMonth() / 3)) + 1,
      year: currentDate.getFullYear()
    };
    this.showPaymentModal = true;
  }

  closePaymentModal(): void {
    this.showPaymentModal = false;
  }

  savePayment(): void {
    if (this.validatePayment()) {
      this.paymentService.addPayment({
        companyId: this.companyId,
        body: this.newPayment
      }).subscribe(
        response => {
          this.showSuccess('Payment saved successfully!');
          this.loadIRSPayments();
          this.loadFinancialData();
          this.closePaymentModal();
        },
        error => {
          console.error('Error saving payment:', error);
          this.showError('Failed to save payment. Please try again.');
        }
      );
    }
  }

  private validatePayment(): boolean {
    if (!this.newPayment.amount || this.newPayment.amount <= 0) {
      this.showError('Please enter a valid amount');
      return false;
    }
    if (!this.newPayment.paymentType) {
      this.showError('Please select a payment type');
      return false;
    }
    if (!this.newPayment.paymentDate) {
      this.showError('Please select a payment date');
      return false;
    }
    return true;
  }

  deletePayment(payment: any): void {
    const paymentId = payment.id || payment.paymentId || payment.paymentHistoryId;
    if (!paymentId) {
      console.error('No payment ID found');
      return;
    }

    if (confirm('Are you sure you want to delete this payment?')) {
      this.paymentService.deletePayment({ paymentId }).subscribe(
        () => {
          this.showSuccess('Payment deleted successfully!');
          this.loadIRSPayments();
          this.loadFinancialData();
        },
        error => {
          console.error('Error deleting payment:', error);
          this.showError('Failed to delete payment. Please try again.');
        }
      );
    }
  }

  // Chart Methods
  private initializeCharts(): void {
    this.createOverviewChart();
    this.createDistributionChart();
  }

  private createOverviewChart(): void {
    const ctx = document.getElementById('overviewChart') as HTMLCanvasElement;
    if (!ctx) return;

    if (this.overviewChart) {
      this.overviewChart.destroy();
    }

    this.overviewChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Company Capital', 'Total Expenses', 'Salary Costs', 'Current Capital'],
        datasets: [{
          label: 'Financial Overview',
          data: [this.companyBudget, this.totalExpenses, this.salaryCost, this.currentCapital],
          backgroundColor: [
            'rgba(91, 71, 224, 0.8)',
            'rgba(255, 87, 87, 0.8)',
            'rgba(57, 175, 234, 0.8)',
            'rgba(0, 217, 126, 0.8)'
          ],
          borderColor: [
            'rgba(91, 71, 224, 1)',
            'rgba(255, 87, 87, 1)',
            'rgba(57, 175, 234, 1)',
            'rgba(0, 217, 126, 1)'
          ],
          borderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              label: (context) => {
                return this.formatCurrency(context.raw as number);
              }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: (value) => this.formatCompactCurrency(value as number)
            }
          }
        }
      }
    });
  }

  private createDistributionChart(): void {
    const ctx = document.getElementById('distributionChart') as HTMLCanvasElement;
    if (!ctx) return;

    if (this.distributionChart) {
      this.distributionChart.destroy();
    }

    const totalUsed = this.totalExpenses + this.salaryCost;
    const remaining = Math.max(0, this.companyBudget - totalUsed);

    this.distributionChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Expenses', 'Salary Costs', 'Remaining Capital'],
        datasets: [{
          data: [this.totalExpenses, this.salaryCost, remaining],
          backgroundColor: [
            'rgba(255, 87, 87, 0.8)',
            'rgba(57, 175, 234, 0.8)',
            'rgba(0, 217, 126, 0.8)'
          ],
          borderColor: [
            'rgba(255, 87, 87, 1)',
            'rgba(57, 175, 234, 1)',
            'rgba(0, 217, 126, 1)'
          ],
          borderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom'
          },
          tooltip: {
            callbacks: {
              label: (context) => {
                const label = context.label || '';
                const value = this.formatCurrency(context.raw as number);
                const total = this.companyBudget;
                const percentage = total > 0 ? ((context.raw as number / total) * 100).toFixed(1) : '0';
                return `${label}: ${value} (${percentage}%)`;
              }
            }
          }
        }
      }
    });
  }

  private updateCharts(): void {
    if (this.overviewChart || this.distributionChart) {
      this.initializeCharts();
    }
  }

  // Utility Methods
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount || 0);
  }

  formatCompactCurrency(amount: number): string {
    if (amount >= 1000000) {
      return '$' + (amount / 1000000).toFixed(1) + 'M';
    } else if (amount >= 1000) {
      return '$' + (amount / 1000).toFixed(0) + 'K';
    }
    return '$' + amount.toFixed(0);
  }

  formatDate(date: string | undefined): string {
    if (!date) return 'N/A';
    return new Date(date).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  }

  formatPaymentType(type: string | undefined): string {
    if (!type) return 'Unknown';
    const typeMap: { [key: string]: string } = {
      'PAYROLL_TAX_941': '941 Tax',
      'UNEMPLOYMENT_TAX_940': '940 Tax',
      'STATE_UNEMPLOYMENT_TAX': 'State Tax',
      'PERSONAL_INSURANCE': 'Insurance',
      'WC_Payment': 'Workers Comp',
      'MCTMT_PREPAYMENT': 'MCTMT Pre',
      'MCTMT_CREDIT': 'MCTMT Credit'
    };
    return typeMap[type] || type;
  }

  getPaymentTypeClass(type: string | undefined): string {
    if (!type) return 'default';
    const classMap: { [key: string]: string } = {
      'PAYROLL_TAX_941': 'payroll',
      'UNEMPLOYMENT_TAX_940': 'unemployment',
      'STATE_UNEMPLOYMENT_TAX': 'state',
      'PERSONAL_INSURANCE': 'insurance',
      'WC_Payment': 'workers-comp',
      'MCTMT_PREPAYMENT': 'mctmt',
      'MCTMT_CREDIT': 'credit'
    };
    return classMap[type] || 'default';
  }

  getCapitalUsagePercent(): number {
    if (!this.companyBudget) return 0;
    const used = this.companyBudget - this.currentCapital;
    return Math.min(100, Math.max(0, (used / this.companyBudget) * 100));
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadIRSPayments();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadIRSPayments();
    }
  }

  toggleFolder(folderKey: string): void {
    this.openFolders[folderKey] = !this.openFolders[folderKey];
  }

  focusChart(chartType: string): void {
    this.focusedChart = this.focusedChart === chartType ? null : chartType;
  }

  toggleFullscreen(chartType: string): void {
    console.log(`Toggling fullscreen for: ${chartType}`);
  }
}
