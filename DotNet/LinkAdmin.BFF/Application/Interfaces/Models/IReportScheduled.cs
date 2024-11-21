namespace LantanaGroup.Link.LinkAdmin.BFF.Application.Interfaces.Models
{
    public interface IReportScheduled
    {        
        string FacilityId { get; set; }        
        List<string> ReportTypes { get; set; }        
        DateTime? StartDate { get; set; }        
        DateTime? EndDate { get; set; }
    }
}
