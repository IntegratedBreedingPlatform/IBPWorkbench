export class GermplasmSearchRequest {
    constructor(
        public nameFilter?: any,
        public germplasmUUID?: string,
        public gid?: number,
        public groupId?: number,
        public sampleUID?: string,
        public germplasmListIds?: Array<number>,
        public stockId?: string,
        public locationOfOrigin?: string,
        public locationOfUse?: string,
        public reference?: number,
        public harvestingStudyIds?: Array<number>,
        public plantingStudyIds?: Array<number>,
        public breedingMethodName?: string,
        public harvestDateFrom?: string,
        public harvestDateTo?: string,
        public femaleParentName?: string,
        public maleParentName?: string,
        public groupSourceName?: string,
        public immediateSourceName?: string,
        public withInventoryOnly?: boolean,
        public withRawObservationsOnly?: boolean,
        public withAnalyzedDataOnly?: boolean,
        public withSampleOnly?: boolean,
        public inProgramListOnly?: boolean,
        public attributes?: any,
        public includePedigree?: any,
        public includeGroupMembers?: boolean,
        public addedColumnsPropertyIds?: Array<string>
    ) {
    }
}
