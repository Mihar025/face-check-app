import 'package:face_check/screens/main_menu/drawer/employee/worksite_employee_screen.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:dio/dio.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../../../api_client/model/work_site_response.dart';
import '../punch_screen/work_site_service.dart';
import '../../../../../providers/localization_provider.dart';

class EmployeeScreen extends StatefulWidget {
  final Dio dio;

  const EmployeeScreen({required this.dio, super.key});

  @override
  State<EmployeeScreen> createState() => _EmployeeScreenState();
}

class _EmployeeScreenState extends State<EmployeeScreen> {
  final TextEditingController _searchController = TextEditingController();
  late final WorkSiteService _workSiteService;
  late final SharedPreferences _prefs;

  List<WorkSiteResponse> workSites = [];
  List<WorkSiteResponse> filteredWorkSites = [];
  bool isLoading = true;
  String? error;

  @override
  void initState() {
    super.initState();
    _workSiteService = WorkSiteService(widget.dio);
    _searchController.addListener(_filterWorkSites);
    _initializePrefs();
    _loadWorkSites();
  }

  Future<void> _initializePrefs() async {
    _prefs = await SharedPreferences.getInstance();
  }

  Future<void> _saveSelectedWorkSite(int workSiteId) async {
    await _prefs.setInt('selected_work_site_id', workSiteId);
    try {
      await _workSiteService.selectWorkSite(workSiteId);
    } catch (e) {
      debugPrint('Error selecting work site: $e');
    }
  }

  Future<void> _loadWorkSites() async {
    try {
      setState(() {
        isLoading = true;
        error = null;
      });

      final sites = await _workSiteService.loadWorkSites();

      setState(() {
        workSites = sites;
        filteredWorkSites = sites;
        isLoading = false;
      });
    } catch (e) {
      final l10n = context.read<LocalizationProvider>().localizations;
      setState(() {
        error = l10n.get('employee.error');
        isLoading = false;
      });
    }
  }

  void _filterWorkSites() {
    if (_searchController.text.isEmpty) {
      setState(() => filteredWorkSites = workSites);
    } else {
      setState(() {
        filteredWorkSites = workSites
            .where((site) => site.workSiteName
            .toString()
            .toLowerCase()
            .contains(_searchController.text.toLowerCase()))
            .toList();
      });
    }
  }

  Widget _buildWorkSiteItem(WorkSiteResponse workSite, bool isSmallScreen) {
    final theme = Theme.of(context);
    final txtColor = theme.textTheme.bodyLarge?.color ?? Colors.white;
    final bgColor = theme.brightness == Brightness.dark
        ? Colors.white.withOpacity(0.1)
        : Colors.black.withOpacity(0.1);

    return InkWell(
      onTap: () {
        if (workSite.workSiteId != null) {
          _saveSelectedWorkSite(workSite.workSiteId!);
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => WorksiteEmployeesScreen(
                worksiteId: workSite.workSiteId!,
              ),
            ),
          );
        }
      },
      child: Container(
        margin: EdgeInsets.symmetric(
            horizontal: isSmallScreen ? 16 : 20,
            vertical: isSmallScreen ? 6 : 8
        ),
        padding: EdgeInsets.all(isSmallScreen ? 12 : 16),
        decoration: BoxDecoration(
          color: bgColor,
          borderRadius: BorderRadius.circular(isSmallScreen ? 10 : 12),
          border: Border.all(
            color: theme.brightness == Brightness.dark
                ? Colors.white.withOpacity(0.1)
                : Colors.black.withOpacity(0.1),
          ),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              workSite.workSiteName.toString(),
              style: TextStyle(
                color: txtColor,
                fontSize: isSmallScreen ? 14 : 16,
                fontWeight: FontWeight.w600,
              ),
            ),
            SizedBox(height: isSmallScreen ? 6 : 8),
            if (workSite.address != null)
              Text(
                workSite.address.toString(),
                style: TextStyle(
                  color: txtColor.withOpacity(0.7),
                  fontSize: isSmallScreen ? 12 : 14,
                ),
              ),
            SizedBox(height: isSmallScreen ? 6 : 8),
            if (workSite.workDayStart != null && workSite.workDayEnd != null)
              Row(
                children: [
                  Icon(
                      Icons.access_time,
                      size: isSmallScreen ? 14 : 16,
                      color: txtColor.withOpacity(0.5)
                  ),
                  SizedBox(width: isSmallScreen ? 3 : 4),
                  Text(
                    '${workSite.workDayStart?.hour ?? 0}:${(workSite.workDayStart?.minute ?? 0).toString().padLeft(2, '0')} - ${workSite.workDayEnd?.hour ?? 0}:${(workSite.workDayEnd?.minute ?? 0).toString().padLeft(2, '0')}',
                    style: TextStyle(
                      color: txtColor.withOpacity(0.7),
                      fontSize: isSmallScreen ? 12 : 14,
                    ),
                  ),
                ],
              ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final l10n = context.read<LocalizationProvider>().localizations;

    // Добавляем адаптивность для разных размеров экрана
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Scaffold(
      appBar: AppBar(
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: EdgeInsets.only(left: isSmallScreen ? 30.0 : 40.0),
              child: Text(
                l10n.get('employee.title'),
                style: GoogleFonts.montserrat(
                  color: theme.textTheme.bodyLarge?.color,
                  fontSize: isSmallScreen ? 18 : 20,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
            Text(
              l10n.get('employee.subtitle'),
              style: GoogleFonts.montserrat(
                color: theme.textTheme.bodyLarge?.color,
                fontSize: isSmallScreen ? 12 : 14,
                fontWeight: FontWeight.w400,
              ),
            ),
          ],
        ),
        backgroundColor: theme.scaffoldBackgroundColor,
        elevation: 0,
        iconTheme: IconThemeData(
            color: theme.iconTheme.color,
            size: isSmallScreen ? 22 : 24
        ),
      ),
      body: Column(
        children: [
          // Search Bar
          Padding(
            padding: EdgeInsets.all(isSmallScreen ? 12.0 : 16.0),
            child: TextField(
              controller: _searchController,
              style: TextStyle(
                  fontSize: isSmallScreen ? 14 : 16
              ),
              decoration: InputDecoration(
                hintText: l10n.get('employee.searchHint'),
                hintStyle: TextStyle(
                    fontSize: isSmallScreen ? 14 : 16
                ),
                prefixIcon: Icon(
                  Icons.search,
                  size: isSmallScreen ? 20 : 24,
                ),
                contentPadding: EdgeInsets.symmetric(
                    vertical: isSmallScreen ? 12 : 16,
                    horizontal: isSmallScreen ? 10 : 12
                ),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(isSmallScreen ? 10 : 12),
                  borderSide: BorderSide(
                    color: theme.brightness == Brightness.dark
                        ? Colors.white.withOpacity(0.1)
                        : Colors.black.withOpacity(0.1),
                  ),
                ),
                enabledBorder: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(isSmallScreen ? 10 : 12),
                  borderSide: BorderSide(
                    color: theme.brightness == Brightness.dark
                        ? Colors.white.withOpacity(0.1)
                        : Colors.black.withOpacity(0.1),
                  ),
                ),
              ),
            ),
          ),
          // Work Sites List
          Expanded(
            child: isLoading
                ? const Center(child: CircularProgressIndicator())
                : error != null
                ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    error!,
                    style: TextStyle(
                        fontSize: isSmallScreen ? 14 : 16
                    ),
                  ),
                  SizedBox(height: isSmallScreen ? 12 : 16),
                  ElevatedButton(
                    onPressed: _loadWorkSites,
                    style: ElevatedButton.styleFrom(
                      padding: EdgeInsets.symmetric(
                          horizontal: isSmallScreen ? 16 : 20,
                          vertical: isSmallScreen ? 8 : 10
                      ),
                    ),
                    child: Text(
                      l10n.get('employee.retry'),
                      style: TextStyle(
                          fontSize: isSmallScreen ? 14 : 16
                      ),
                    ),
                  ),
                ],
              ),
            )
                : ListView.builder(
              itemCount: filteredWorkSites.length,
              padding: EdgeInsets.only(top: isSmallScreen ? 6 : 8),
              itemBuilder: (context, index) {
                return _buildWorkSiteItem(filteredWorkSites[index], isSmallScreen);
              },
            ),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }
}