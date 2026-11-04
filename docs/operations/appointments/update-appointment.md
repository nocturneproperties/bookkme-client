[← Operations](../README.md)

# Update appointment (reschedule)

`UpdateAppointment(appointment)` → `PUT /api/appointments/{id}`
· called from `AppointmentDetailsViewModel`

The body is an `AppointmentUpdate`, which carries only ids: `id` (#1), `date` (#8), `note` (#9),
`employeeId` (#13) and `services` (#14) as `RequestedService(serviceId, count)`. The proto numbers are
deliberately sparse because they match the fields of the backend `Appointment`. `Appointment.toUpdateRemote()`
builds `services` by grouping the appointment's service snapshots by id, since the backend stores one
snapshot per booked count, and keeps booking order. The backend keeps the stored client and business, and
keeps the stored price of every service that is already on the appointment.

The server response is saved to the database and returned.

```mermaid
flowchart TD
    Start([invoke appointment]) --> Map[Appointment.toUpdateRemote<br/>services grouped by id into RequestedService serviceId, count]
    Map --> Net[AppointmentDataSource.updateAppointment<br/>PUT /api/appointments/id body AppointmentUpdate]
    Net -- 2xx result --> Save[(saveAppointmentInDB result<br/>upsertWithServices)]
    Save --> R([return result])
    Net -- business error --> Code{errorCode}
    Code -- 300003 DATE_NOT_ALLOWED --> E1([throw Error.DateIsNotAllowed])
    Code -- 300002 TIME_NOT_ALLOWED --> E2([throw Error.TimeIsNotAllowed])
    Code -- 300004 APPOINTMENT_EXISTS --> E3([throw Error.AppointmentOverlap])
    Code -- 300005 / 300006 / 300019 --> E4([throw Error.AppointmentNotScheduled])
    Code -- 200033 BUSINESS_EMPLOYEE_SUSPENDED --> E5([throw Error.EmployeeSuspended])
    Code -- 300012 --> Added300012([throw Error.DateInPast])
    Code -- 300024 --> Added300024([throw Error.InvalidServiceSelection])
    Code -- 200013 --> Added200013([throw Error.ServiceNotFound])
    Code -- 200024 --> Added200024([throw Error.EmployeeNotFound])
    Code -- 900001 --> Added900001([throw Error.ConcurrentModification])
    Code -- 300026 --> Added300026([throw Error.EditWindowClosed])
    Code -- other --> EX([rethrow])
```

A transaction conflict is reported as `ConcurrentModification` without automatically retrying; callers
can choose to reload rather than replaying a stale appointment over newer server changes.

`EditWindowClosed` means the appointment ended more than seven days ago.
