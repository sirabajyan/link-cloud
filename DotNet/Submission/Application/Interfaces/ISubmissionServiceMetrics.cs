namespace LantanaGroup.Link.Submission.Application.Interfaces
{
    public interface ISubmissionServiceMetrics
    {
        void IncrementResourcesSubmittedCounter(int resourcesSubmitted, List<KeyValuePair<string, object?>> tags);
        void IncrementResourceTypeCounter(int resourceTypeCount, List<KeyValuePair<string, object?>> tags);
        void IncrementMedicationCounter(int medCodeCount, List<KeyValuePair<string, object?>> tags);
    }
}
