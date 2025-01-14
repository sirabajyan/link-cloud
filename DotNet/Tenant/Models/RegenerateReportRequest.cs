using System.ComponentModel.DataAnnotations;

namespace LantanaGroup.Link.Tenant.Models
{
    public class RegenerateReportRequest
    {
        [Required] public string? ReportId { get; set; }
        public bool? BypassSubmission { get; set; }
    }
}