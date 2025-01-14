using System.ComponentModel.DataAnnotations;

namespace LantanaGroup.Link.Tenant.Models
{
    public class AdHocReportRequest
    {
        public bool? BypassSubmission { get; set; }
        [Required]
        public DateTime? StartDate { get; set; }
        [Required]
        public DateTime? EndDate { get; set; }
        [Required]
        public List<string>? ReportTypes { get; set; }
        public List<string>? PatientIds { get; set; }

    }
}
