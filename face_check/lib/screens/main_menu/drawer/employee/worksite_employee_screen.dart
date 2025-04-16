import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../../../../providers/localization_provider.dart';
import '../../../../services/ApiService.dart';
import '../../../../models/worksite_worker_response.dart';

class WorksiteEmployeesScreen extends StatefulWidget {
  final int worksiteId;

  const WorksiteEmployeesScreen({
    required this.worksiteId,
    super.key,
  });

  @override
  State<WorksiteEmployeesScreen> createState() => _WorksiteEmployeesScreenState();
}

class _WorksiteEmployeesScreenState extends State<WorksiteEmployeesScreen> {
  final ApiService _apiService = ApiService.instance;
  bool isLoading = true;
  String? error;
  List<WorksiteWorkerResponse> workers = [];
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    _loadWorkers();
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  Future<void> _loadWorkers() async {
    try {
      setState(() {
        isLoading = true;
        error = null;
      });

      final response = await _apiService.getWorkersInWorksite(
        worksiteId: widget.worksiteId,
        page: 0,
      );

      setState(() {
        workers = response.content ?? [];
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        error = 'Cannot load employee';
        isLoading = false;
      });
    }
  }

  Widget _buildWorkerItem(WorksiteWorkerResponse worker) {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    final initials = _getInitials(worker.firstName, worker.lastName);
    final avatarColor = _getAvatarColor(worker.workerId);

    return Card(
      margin: EdgeInsets.symmetric(
          horizontal: isSmallScreen ? 16 : 20,
          vertical: isSmallScreen ? 6 : 8
      ),
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: BorderSide(
          color: Colors.grey.withOpacity(0.2),
          width: 1,
        ),
      ),
      child: Padding(
        padding: EdgeInsets.all(isSmallScreen ? 12 : 16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                // Avatar with initials
                CircleAvatar(
                  radius: isSmallScreen ? 22 : 26,
                  backgroundColor: avatarColor,
                  child: Text(
                    initials,
                    style: TextStyle(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                      fontSize: isSmallScreen ? 16 : 18,
                    ),
                  ),
                ),
                SizedBox(width: isSmallScreen ? 12 : 16),

                // Name and ID
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        '${worker.firstName} ${worker.lastName}',
                        style: GoogleFonts.poppins(
                          fontSize: isSmallScreen ? 16 : 18,
                          fontWeight: FontWeight.w600,
                        ),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                      ),
                      if (worker.workerId != null)
                        Container(
                          margin: EdgeInsets.only(top: isSmallScreen ? 3 : 4),
                          padding: EdgeInsets.symmetric(
                              horizontal: isSmallScreen ? 8 : 10,
                              vertical: isSmallScreen ? 2 : 3
                          ),
                          decoration: BoxDecoration(
                            color: Theme.of(context).primaryColor.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(50),
                          ),
                          child: Text(
                            'ID: ${worker.workerId}',
                            style: GoogleFonts.poppins(
                              fontSize: isSmallScreen ? 10 : 12,
                              fontWeight: FontWeight.w500,
                              color: Theme.of(context).primaryColor,
                            ),
                          ),
                        ),
                    ],
                  ),
                ),
              ],
            ),

            SizedBox(height: isSmallScreen ? 16 : 20),

            // Contact info and status
            _buildInfoSection(worker),

            SizedBox(height: isSmallScreen ? 12 : 16),

            // Divider before actions
            const Divider(height: 1),

            // Action buttons
            if (worker.punchIn != null)
              Padding(
                padding: EdgeInsets.only(top: isSmallScreen ? 12 : 16),
                child: Row(
                  children: [
                    Expanded(
                      child: _buildActionButton(
                        label: 'Delete Punch In',
                        icon: Icons.delete_outline_rounded,
                        color: Colors.redAccent,
                        onPressed: () => _confirmDeletePunchIn(worker),
                      ),
                    ),
                    SizedBox(width: isSmallScreen ? 8 : 12),
                    Expanded(
                      child: _buildActionButton(
                        label: 'Update Time',
                        icon: Icons.edit_calendar_outlined,
                        color: Theme.of(context).primaryColor,
                        onPressed: () => _updatePunchInTime(worker),
                      ),
                    ),
                  ],
                ),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoSection(WorksiteWorkerResponse worker) {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Column(
      children: [
        if (worker.phoneNumber != null)
          _buildInfoRow(
            icon: Icons.phone_rounded,
            label: worker.phoneNumber!,
            iconColor: Colors.green,
            isSmallScreen: isSmallScreen,
          ),

        if (worker.workSiteAddress != null)
          Padding(
            padding: EdgeInsets.only(top: isSmallScreen ? 8 : 12),
            child: _buildInfoRow(
              icon: Icons.location_on_rounded,
              label: worker.workSiteAddress!,
              iconColor: Colors.orangeAccent,
              isSmallScreen: isSmallScreen,
            ),
          ),

        if (worker.punchIn != null)
          Container(
            margin: EdgeInsets.only(top: isSmallScreen ? 8 : 12),
            padding: EdgeInsets.symmetric(
                horizontal: isSmallScreen ? 10 : 12,
                vertical: isSmallScreen ? 8 : 10
            ),
            decoration: BoxDecoration(
              color: Theme.of(context).primaryColor.withOpacity(0.08),
              borderRadius: BorderRadius.circular(12),
            ),
            child: _buildInfoRow(
              icon: Icons.login_rounded,
              label: 'Время прихода: ${_formatDateTime(worker.punchIn!)}',
              iconColor: Theme.of(context).primaryColor,
              isSmallScreen: isSmallScreen,
              textStyle: GoogleFonts.poppins(
                fontSize: isSmallScreen ? 12 : 14,
                fontWeight: FontWeight.w500,
                color: Theme.of(context).primaryColor,
              ),
            ),
          ),
      ],
    );
  }

  Widget _buildInfoRow({
    required IconData icon,
    required String label,
    required Color iconColor,
    required bool isSmallScreen,
    TextStyle? textStyle,
  }) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Icon(
          icon,
          size: isSmallScreen ? 18 : 20,
          color: iconColor,
        ),
        SizedBox(width: isSmallScreen ? 8 : 10),
        Expanded(
          child: Text(
            label,
            style: textStyle ?? GoogleFonts.poppins(
              fontSize: isSmallScreen ? 12 : 14,
              color: Theme.of(context).textTheme.bodyMedium?.color,
              height: 1.3,
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildActionButton({
    required String label,
    required IconData icon,
    required Color color,
    required VoidCallback onPressed,
  }) {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return ElevatedButton.icon(
      onPressed: onPressed,
      icon: Icon(icon, size: isSmallScreen ? 18 : 20, color: color),
      label: Text(
        label,
        style: GoogleFonts.poppins(
          fontSize: isSmallScreen ? 11 : 13,
          fontWeight: FontWeight.w500,
          color: color,
        ),
      ),
      style: ElevatedButton.styleFrom(
        elevation: 0,
        backgroundColor: color.withOpacity(0.1),
        padding: EdgeInsets.symmetric(vertical: isSmallScreen ? 10 : 12),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
        ),
      ),
    );
  }

  Future<DateTime?> showTimeInputDialog(
      BuildContext context, {
        DateTime? initialTime,
      }) async {
    final DateTime now = DateTime.now();
    final DateTime today = DateTime(now.year, now.month, now.day);

    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    final initialHour = initialTime != null ? initialTime.hour : now.hour;
    final initialMinute = initialTime != null ? initialTime.minute : now.minute;

    final displayHour = initialHour > 12 ? initialHour - 12 : (initialHour == 0 ? 12 : initialHour);

    final TextEditingController hourController = TextEditingController(text: displayHour.toString().padLeft(2, '0'));
    final TextEditingController minuteController = TextEditingController(text: initialMinute.toString().padLeft(2, '0'));

    bool isAM = initialHour < 12;

    final FocusNode hourFocus = FocusNode();
    final FocusNode minuteFocus = FocusNode();

    DateTime? result;

    await showDialog<void>(
      context: context,
      builder: (BuildContext context) {
        return StatefulBuilder(
          builder: (context, setState) {
            Color primaryColorMuted = HSLColor.fromColor(Theme.of(context).primaryColor)
                .withSaturation(0.6)
                .withLightness(0.5)
                .toColor();

            return Theme(
              data: ThemeData.dark().copyWith(
                primaryColor: primaryColorMuted,
                colorScheme: ColorScheme.dark(
                  primary: primaryColorMuted,
                  secondary: primaryColorMuted,
                ),
                inputDecorationTheme: InputDecorationTheme(
                  border: OutlineInputBorder(
                    borderSide: BorderSide(color: Colors.grey[700]!),
                  ),
                  enabledBorder: OutlineInputBorder(
                    borderSide: BorderSide(color: Colors.grey[600]!),
                  ),
                  focusedBorder: OutlineInputBorder(
                    borderSide: BorderSide(color: primaryColorMuted, width: 2),
                  ),
                ),
              ),
              child: AlertDialog(
                backgroundColor: Colors.grey[850],
                title: Text(
                  'Enter time',
                  style: GoogleFonts.poppins(
                    fontWeight: FontWeight.w600,
                    color: Colors.white,
                    fontSize: isSmallScreen ? 18 : 20,
                  ),
                ),
                content: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        // Hour input
                        SizedBox(
                          width: isSmallScreen ? 50 : 60,
                          child: TextField(
                            controller: hourController,
                            focusNode: hourFocus,
                            keyboardType: TextInputType.number,
                            textAlign: TextAlign.center,
                            style: TextStyle(
                              fontSize: isSmallScreen ? 20 : 24,
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                            ),
                            decoration: InputDecoration(
                              hintText: '00',
                              hintStyle: TextStyle(color: Colors.grey[500]),
                              contentPadding: EdgeInsets.symmetric(
                                  vertical: isSmallScreen ? 8 : 12,
                                  horizontal: isSmallScreen ? 6 : 8
                              ),
                            ),
                            onChanged: (value) {
                              if (value.length == 2) {
                                minuteFocus.requestFocus();
                              }
                            },
                            inputFormatters: [
                              LengthLimitingTextInputFormatter(2),
                              FilteringTextInputFormatter.digitsOnly,
                            ],
                          ),
                        ),

                        Padding(
                          padding: EdgeInsets.symmetric(
                              horizontal: isSmallScreen ? 6 : 10
                          ),
                          child: Text(
                            ':',
                            style: TextStyle(
                              fontSize: isSmallScreen ? 20 : 24,
                              fontWeight: FontWeight.bold,
                              color: Colors.white,
                            ),
                          ),
                        ),

                        // Minute input
                        SizedBox(
                          width: isSmallScreen ? 50 : 60,
                          child: TextField(
                            controller: minuteController,
                            focusNode: minuteFocus,
                            keyboardType: TextInputType.number,
                            textAlign: TextAlign.center,
                            style: TextStyle(
                              fontSize: isSmallScreen ? 20 : 24,
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                            ),
                            decoration: InputDecoration(
                              hintText: '00',
                              hintStyle: TextStyle(color: Colors.grey[500]),
                              contentPadding: EdgeInsets.symmetric(
                                  vertical: isSmallScreen ? 8 : 12,
                                  horizontal: isSmallScreen ? 6 : 8
                              ),
                            ),
                            inputFormatters: [
                              LengthLimitingTextInputFormatter(2),
                              FilteringTextInputFormatter.digitsOnly,
                            ],
                          ),
                        ),

                        SizedBox(width: isSmallScreen ? 10 : 15),
                        Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            ElevatedButton(
                              onPressed: () {
                                setState(() {
                                  isAM = true;
                                });
                              },
                              style: ElevatedButton.styleFrom(
                                backgroundColor: isAM
                                    ? primaryColorMuted
                                    : Colors.grey[700],
                                foregroundColor: Colors.white70,
                                minimumSize: Size(isSmallScreen ? 40 : 50, isSmallScreen ? 32 : 36),
                                padding: EdgeInsets.symmetric(
                                    horizontal: isSmallScreen ? 4 : 8,
                                    vertical: 0
                                ),
                                elevation: isAM ? 1 : 0,
                              ),
                              child: Text(
                                'AM',
                                style: TextStyle(
                                    fontSize: isSmallScreen ? 11 : 13
                                ),
                              ),
                            ),
                            SizedBox(height: isSmallScreen ? 6 : 8),

                            ElevatedButton(
                              onPressed: () {
                                setState(() {
                                  isAM = false;
                                });
                              },
                              style: ElevatedButton.styleFrom(
                                backgroundColor: !isAM
                                    ? primaryColorMuted
                                    : Colors.grey[700],
                                foregroundColor: Colors.white70,
                                minimumSize: Size(isSmallScreen ? 40 : 50, isSmallScreen ? 32 : 36),
                                padding: EdgeInsets.symmetric(
                                    horizontal: isSmallScreen ? 4 : 8,
                                    vertical: 0
                                ),
                                elevation: !isAM ? 1 : 0,
                              ),
                              child: Text(
                                'PM',
                                style: TextStyle(
                                    fontSize: isSmallScreen ? 11 : 13
                                ),
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                    SizedBox(height: isSmallScreen ? 6 : 8),
                    Text(
                      'Todays date will be used!',
                      style: GoogleFonts.poppins(
                        fontSize: isSmallScreen ? 10 : 12,
                        color: Colors.grey[400],
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
                actions: [
                  TextButton(
                    onPressed: () {
                      Navigator.of(context).pop();
                    },
                    child: Text(
                      'Cancel',
                      style: GoogleFonts.poppins(
                        color: Colors.grey[400],
                        fontSize: isSmallScreen ? 12 : 14,
                      ),
                    ),
                  ),
                  ElevatedButton(
                    onPressed: () {

                      int hour;
                      int minute;

                      try {
                        hour = int.parse(hourController.text);
                        minute = int.parse(minuteController.text);


                        if (hour < 1 || hour > 12) {
                          throw FormatException('Wrong hour format');
                        }

                        if (minute < 0 || minute > 59) {
                          throw FormatException('Wrong minute format');
                        }

                        if (hour == 12) {
                          hour = isAM ? 0 : 12;
                        } else if (!isAM) {
                          hour += 12;
                        }

                        result = DateTime(
                          today.year,
                          today.month,
                          today.day,
                          hour,
                          minute,
                        );

                        Navigator.of(context).pop();
                      } catch (e) {
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(
                            content: Text(
                              'Write well formatted time!',
                              style: TextStyle(
                                fontSize: isSmallScreen ? 12 : 14,
                              ),
                            ),
                            backgroundColor: Colors.red,
                          ),
                        );
                      }
                    },
                    style: ElevatedButton.styleFrom(
                      backgroundColor: primaryColorMuted,
                      foregroundColor: Colors.white,
                      padding: EdgeInsets.symmetric(
                          horizontal: isSmallScreen ? 12 : 16,
                          vertical: isSmallScreen ? 6 : 8
                      ),
                    ),
                    child: Text(
                      'Ok',
                      style: GoogleFonts.poppins(
                        fontSize: isSmallScreen ? 12 : 14,
                      ),
                    ),
                  ),
                ],
              ),
            );
          },
        );
      },
    );

    // Clean up
    hourController.dispose();
    minuteController.dispose();
    hourFocus.dispose();
    minuteFocus.dispose();

    return result;
  }





  Future<void> _confirmDeletePunchIn(WorksiteWorkerResponse worker) async {
    if (worker.workerId == null) return;
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    final shouldDelete = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(
          'Confirm',
          style: GoogleFonts.poppins(
            fontWeight: FontWeight.w600,
            fontSize: isSmallScreen ? 18 : 20,
          ),
        ),
        content: Text(
          'Are you sure, that you want it??',
          style: GoogleFonts.poppins(
            fontSize: isSmallScreen ? 14 : 16,
          ),
        ),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(20),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: Text(
              'Cancel',
              style: GoogleFonts.poppins(
                color: Colors.grey[700],
                fontSize: isSmallScreen ? 12 : 14,
              ),
            ),
          ),
          ElevatedButton(
            onPressed: () => Navigator.of(context).pop(true),
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.redAccent,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
              ),
              padding: EdgeInsets.symmetric(
                  horizontal: isSmallScreen ? 12 : 16,
                  vertical: isSmallScreen ? 8 : 10
              ),
            ),
            child: Text(
              'Delete',
              style: GoogleFonts.poppins(
                color: Colors.white,
                fontSize: isSmallScreen ? 12 : 14,
              ),
            ),
          ),
        ],
      ),
    ) ?? false;

    if (shouldDelete) {
      try {
        await ApiService.instance.deleteWorkerPunchIn(worker.workerId!);
        _showSuccessSnackBar('Punch In deleted');
        _refreshWorkersList();
      } catch (e) {
        _showErrorSnackBar('Error: ${e.toString()}');
      }
    }
  }

  Future<void> _updatePunchInTime(WorksiteWorkerResponse worker) async {
    if (worker.workerId == null) return;

    try {
      final pickedDateTime = await showTimeInputDialog(
        context,
        initialTime: worker.punchIn,
      );

      if (pickedDateTime != null) {
        final response = await ApiService.instance.updatePunchInTime(
          worker.workerId!,
          pickedDateTime,
        );

        if (response != null) {
          _showSuccessSnackBar('Time updated: ${_formatDateTime(response.newPunchInTime, includeDate: true)}');
        } else {
          _showSuccessSnackBar('Time successfully updated');
        }
        _refreshWorkersList();
      }
    } catch (e) {
      _showErrorSnackBar('error: ${e.toString()}');
    }
  }

  void _showSuccessSnackBar(String message) {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    final snackBar = SnackBar(
      content: Row(
        children: [
          Icon(Icons.check_circle_outline,
              color: Colors.white,
              size: isSmallScreen ? 18 : 20),
          SizedBox(width: isSmallScreen ? 8 : 12),
          Expanded(
            child: Text(
              message,
              style: GoogleFonts.poppins(
                fontSize: isSmallScreen ? 12 : 14,
              ),
            ),
          ),
        ],
      ),
      backgroundColor: Colors.green,
      behavior: SnackBarBehavior.floating,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      margin: EdgeInsets.all(isSmallScreen ? 8 : 12),
    );
    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }

  void _showErrorSnackBar(String message) {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    final snackBar = SnackBar(
      content: Row(
        children: [
          Icon(Icons.error_outline,
              color: Colors.white,
              size: isSmallScreen ? 18 : 20),
          SizedBox(width: isSmallScreen ? 8 : 12),
          Expanded(
            child: Text(
              message,
              style: GoogleFonts.poppins(
                fontSize: isSmallScreen ? 12 : 14,
              ),
            ),
          ),
        ],
      ),
      backgroundColor: Colors.redAccent,
      behavior: SnackBarBehavior.floating,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      margin: EdgeInsets.all(isSmallScreen ? 8 : 12),
    );
    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }

  Widget _buildEmptyState() {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Center(
      child: SingleChildScrollView(
        physics: const AlwaysScrollableScrollPhysics(),
        child: Padding(
          padding: EdgeInsets.all(isSmallScreen ? 24.0 : 30.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Image.asset(
                'assets/images/empty_state.png',
                height: isSmallScreen ? 150 : 180,
                fit: BoxFit.contain,
                errorBuilder: (context, error, stackTrace) => Icon(
                  Icons.people_alt_outlined,
                  size: isSmallScreen ? 100 : 120,
                  color: Colors.grey,
                ),
              ),
              SizedBox(height: isSmallScreen ? 24 : 32),
              Text(
                'Nobody did Punch In',
                style: GoogleFonts.poppins(
                  fontSize: isSmallScreen ? 18 : 20,
                  fontWeight: FontWeight.w600,
                  color: Colors.grey[800],
                ),
                textAlign: TextAlign.center,
              ),
              SizedBox(height: isSmallScreen ? 8 : 12),
              Text(
                'Persons whose did Punch In, will be displayed here.',
                style: GoogleFonts.poppins(
                  fontSize: isSmallScreen ? 14 : 16,
                  color: Colors.grey[600],
                  height: 1.5,
                ),
                textAlign: TextAlign
                    .center,
              ),
              SizedBox(height: isSmallScreen ? 24 : 30),
              ElevatedButton.icon(
                onPressed: _refreshWorkersList,
                icon: Icon(
                  Icons.refresh_rounded,
                  size: isSmallScreen ? 18 : 20,
                ),
                label: Text(
                  'Refresh',
                  style: GoogleFonts.poppins(
                    fontWeight: FontWeight.w500,
                    fontSize: isSmallScreen ? 12 : 14,
                  ),
                ),
                style: ElevatedButton.styleFrom(
                  padding: EdgeInsets.symmetric(
                      horizontal: isSmallScreen ? 20 : 24,
                      vertical: isSmallScreen ? 10 : 12
                  ),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(50),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildErrorState() {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;

    return Center(
      child: Padding(
        padding: EdgeInsets.all(isSmallScreen ? 24.0 : 30.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.cloud_off_rounded,
              size: isSmallScreen ? 65 : 80,
              color: Colors.grey,
            ),
            SizedBox(height: isSmallScreen ? 20 : 24),
            Text(
              'Something went wrong',
              style: GoogleFonts.poppins(
                fontSize: isSmallScreen ? 18 : 20,
                fontWeight: FontWeight.w600,
                color: Colors.grey[800],
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: isSmallScreen ? 8 : 12),
            Text(
              error ?? 'Cannot load the data! Try again.',
              style: GoogleFonts.poppins(
                fontSize: isSmallScreen ? 14 : 16,
                color: Colors.grey[600],
                height: 1.5,
              ),
              textAlign: TextAlign.center,
            ),
            SizedBox(height: isSmallScreen ? 24 : 30),
            ElevatedButton.icon(
              onPressed: () {
                setState(() {
                  workers.clear();
                });
                _loadWorkers();
              },
              icon: Icon(
                Icons.refresh_rounded,
                size: isSmallScreen ? 18 : 20,
              ),
              label: Text(
                'Try again',
                style: GoogleFonts.poppins(
                  fontWeight: FontWeight.w500,
                  fontSize: isSmallScreen ? 12 : 14,
                ),
              ),
              style: ElevatedButton.styleFrom(
                padding: EdgeInsets.symmetric(
                    horizontal: isSmallScreen ? 20 : 24,
                    vertical: isSmallScreen ? 10 : 12
                ),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(50),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _formatDateTime(DateTime dateTime, {bool includeDate = false}) {
    if (includeDate) {
      final DateFormat formatter = DateFormat('dd.MM.yyyy HH:mm');
      return formatter.format(dateTime);
    } else {
      return '${dateTime.hour.toString().padLeft(2, '0')}:${dateTime.minute.toString().padLeft(2, '0')}';
    }
  }

  String _getInitials(String? firstName, String? lastName) {
    String firstInitial = (firstName?.isNotEmpty == true) ? firstName![0].toUpperCase() : '';
    String lastInitial = (lastName?.isNotEmpty == true) ? lastName![0].toUpperCase() : '';

    if (firstInitial.isEmpty && lastInitial.isEmpty) {
      return '?';
    } else if (firstInitial.isEmpty) {
      return lastInitial;
    } else if (lastInitial.isEmpty) {
      return firstInitial;
    } else {
      return '$firstInitial$lastInitial';
    }
  }

  Color _getAvatarColor(int? id) {
    final List<Color> colors = [
      Colors.blueAccent,
      Colors.redAccent,
      Colors.greenAccent,
      Colors.purpleAccent,
      Colors.orangeAccent,
      Colors.teal,
      Colors.pinkAccent,
      Colors.indigoAccent,
    ];

    if (id == null) return colors[0];
    return colors[id % colors.length];
  }

  @override
  Widget build(BuildContext context) {
    final screenSize = MediaQuery.of(context).size;
    final isSmallScreen = screenSize.width < 360;
    final l10n = context.read<LocalizationProvider>().localizations;

    return Scaffold(
      backgroundColor: Colors.grey[50],
      appBar: AppBar(
        elevation: 0,
        scrolledUnderElevation: 1,
        centerTitle: false,
        title: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              l10n.get('worksiteEmployees.title'),
              style: GoogleFonts.poppins(
                fontSize: isSmallScreen ? 18 : 20,
                fontWeight: FontWeight.w600,
              ),
            ),
            Text(
              'Work Site #${widget.worksiteId}',
              style: GoogleFonts.poppins(
                fontSize: isSmallScreen ? 11 : 13,
                fontWeight: FontWeight.normal,
                color: Colors.grey[600],
              ),
            ),
          ],
        ),
        actions: [
          if (isLoading)
            Padding(
              padding: EdgeInsets.only(right: isSmallScreen ? 12 : 16),
              child: SizedBox(
                width: isSmallScreen ? 18 : 20,
                height: isSmallScreen ? 18 : 20,
                child: CircularProgressIndicator(
                  strokeWidth: isSmallScreen ? 1.5 : 2,
                  color: Theme.of(context).primaryColor,
                ),
              ),
            ),
          IconButton(
            icon: Icon(
              Icons.refresh_rounded,
              size: isSmallScreen ? 22 : 24,
            ),
            tooltip: 'Refresh',
            onPressed: isLoading ? null : _refreshWorkersList,
          ),
          SizedBox(width: isSmallScreen ? 6 : 8),
        ],
      ),
      body: error != null
          ? _buildErrorState()
          : isLoading && workers.isEmpty
          ? Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(
              strokeWidth: isSmallScreen ? 3 : 4,
            ),
            SizedBox(height: isSmallScreen ? 12 : 16),
            Text(
              'Loading employee...',
              style: GoogleFonts.poppins(
                fontSize: isSmallScreen ? 12 : 14,
                color: Colors.grey[600],
              ),
            ),
          ],
        ),
      )
          : workers.isEmpty
          ? _buildEmptyState()
          : RefreshIndicator(
        onRefresh: () async {
          await _loadWorkers();
        },
        child: ListView.builder(
          controller: _scrollController,
          physics: const AlwaysScrollableScrollPhysics(),
          padding: EdgeInsets.only(
              top: isSmallScreen ? 8 : 12,
              bottom: isSmallScreen ? 20 : 24
          ),
          itemCount: workers.length,
          itemBuilder: (context, index) {
            return _buildWorkerItem(workers[index]);
          },
        ),
      ),
    );
  }

  void _refreshWorkersList() {
    if (isLoading) return;

    setState(() {
      workers.clear();
      isLoading = true;
      error = null;
    });

    _loadWorkers();
  }
}