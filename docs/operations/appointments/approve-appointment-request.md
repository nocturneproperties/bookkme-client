[← Operations](../README.md)

# Approve appointment request

`ApproveAppointmentRequest(requestId)` → `POST /api/appointments` (body `AppointmentRequestIdRemote`)
· called from `AppointmentRequestViewModel`

Turns a pending request into a real appointment on the backend. The new appointment is saved to the
`appointment` table, and the approved `appointment_request` row is deleted, each in its own datasource call.
`AppointmentListViewModel` redraws the day and the request count from those tables.

The slot must fit the business schedule and the requested employee's current schedule, checked in that
order. Approval also fails when that employee has since been suspended (422) or removed (404).

```mermaid
flowchart TD
    Start([invoke requestId]) --> Net[AppointmentRequestDataSource.createAppointmentFromRequest<br/>POST /api/appointments]
    Net -- 2xx result --> Save[(AppointmentDataSource.saveAppointmentInDB result<br/>upsertWithServices)]
    Save --> Del[(AppointmentRequestDataSource.deleteAppointmentRequestsInDb requestId<br/>appointmentRequestDao.deleteByIds)]
    Del --> R([return Appointment])
    Net -- business error --> Code{errorCode}
    Code -- 300004 APPOINTMENT_EXISTS --> E1([throw Error.AppointmentExists])
    Code -- 300003 BUSINESS_DATE_NOT_ALLOWED --> E2([throw Error.DateNotAllowed])
    Code -- 300002 BUSINESS_TIME_NOT_ALLOWED --> E3([throw Error.TimeNotAllowed])
    Code -- 300012 DATE_IN_PAST --> E4([throw Error.DateInPast])
    Code -- 300027 EMPLOYEE_DATE_NOT_ALLOWED --> E5([throw Error.EmployeeDateNotAllowed])
    Code -- 300028 EMPLOYEE_TIME_NOT_ALLOWED --> E6([throw Error.EmployeeTimeNotAllowed])
    Code -- 200033 BUSINESS_EMPLOYEE_SUSPENDED --> E7([throw Error.EmployeeSuspended])
    Code -- 200024 BUSINESS_EMPLOYEE_NOT_EXISTS --> E8([throw Error.EmployeeNotFound])
    Code -- other --> EX([rethrow])
    Net -- network / other --> EX
```
