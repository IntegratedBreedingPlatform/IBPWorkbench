
package org.generationcp.breeding.manager.listmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.swing.text.html.Option;

import com.google.common.collect.Table;
import org.apache.poi.common.usermodel.Fill;
import org.generationcp.breeding.manager.listmanager.api.FillColumnSource;
import org.generationcp.breeding.manager.listmanager.util.FillWithOption;
import org.generationcp.breeding.manager.util.BreedingManagerUtil;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

@Configurable
public class GermplasmColumnValuesGenerator {

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private PedigreeService pedigreeService;

	@Resource
	private CrossExpansionProperties crossExpansionProperties;

	@Resource
	private PedigreeDataManager pedigreeDataManager;

	private final FillColumnSource fillColumnSource;

	public GermplasmColumnValuesGenerator(final FillColumnSource fillColumnSource) {
		this.fillColumnSource = fillColumnSource;
	}

	public void setPreferredIdColumnValues(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			final List<Integer> gids = this.fillColumnSource.getGidsToProcess();
			final Map<Integer, String> gidPreferredIDsMap = this.germplasmDataManager.getPreferredIdsByGIDs(gids);

			for (final Object itemId : itemIds) {
				final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
				String preferredID = "";
				if (gidPreferredIDsMap.get(gid) != null) {
					preferredID = gidPreferredIDsMap.get(gid);
				}
				this.fillColumnSource.setColumnValueForItem(itemId, columnName, preferredID);
			}

			this.fillColumnSource.propagateUIChanges();
		}
	}

	public void setPreferredNameColumnValues(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			final List<Integer> gids = this.fillColumnSource.getGidsToProcess();
			final Map<Integer, String> gidPreferredNamesMap = this.germplasmDataManager.getPreferredNamesByGids(gids);
			for (final Object itemId : itemIds) {
				final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
				String preferredName = "";
				if (gidPreferredNamesMap.get(gid) != null) {
					preferredName = gidPreferredNamesMap.get(gid);
				}
				this.fillColumnSource.setColumnValueForItem(itemId, columnName, preferredName);
			}

			this.fillColumnSource.propagateUIChanges();
		}
	}

	public void setGermplasmDateColumnValues(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			final List<Integer> gids = this.fillColumnSource.getGidsToProcess();
			final Map<Integer, Integer> germplasmGidDateMap = this.germplasmDataManager.getGermplasmDatesByGids(gids);
			for (final Object itemId : itemIds) {
				final Integer gid = this.fillColumnSource.getGidForItemId(itemId);

				if (germplasmGidDateMap.get(gid) == null) {
					this.fillColumnSource.setColumnValueForItem(itemId, columnName, "");
				} else {
					this.fillColumnSource.setColumnValueForItem(itemId, columnName, germplasmGidDateMap.get(gid));
				}
			}
			this.fillColumnSource.propagateUIChanges();
		}

	}

	public void setLocationNameColumnValues(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			final List<Integer> gids = this.fillColumnSource.getGidsToProcess();
			final Map<Integer, String> locationNamesMap = this.germplasmDataManager.getLocationNamesByGids(gids);

			for (final Object itemId : itemIds) {
				final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
				if (locationNamesMap.get(gid) == null) {
					this.fillColumnSource.setColumnValueForItem(itemId, columnName, "");
				} else {
					this.fillColumnSource.setColumnValueForItem(itemId, columnName, locationNamesMap.get(gid));
				}
			}

			this.fillColumnSource.propagateUIChanges();
		}
	}

	public void setMethodInfoColumnValues(final String columnName, final FillWithOption option) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			final List<Integer> gids = this.fillColumnSource.getGidsToProcess();
			final Map<Integer, Object> methodsMap = this.germplasmDataManager.getMethodsByGids(gids);

			for (final Object itemId : itemIds) {
				final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
				final String value = this.getBreedingMethod(option, methodsMap, gid);
				this.fillColumnSource.setColumnValueForItem(itemId, columnName, value);

			}
			this.fillColumnSource.propagateUIChanges();
		}

	}

	private String getBreedingMethod(final FillWithOption option, final Map<Integer, Object> methodsMap, final Integer gid) {
		if (methodsMap.get(gid) != null) {
			switch (option) {
				case FILL_WITH_BREEDING_METHOD_NAME:
					return ((Method) methodsMap.get(gid)).getMname();
				case FILL_WITH_BREEDING_METHOD_ABBREV:
					return ((Method) methodsMap.get(gid)).getMcode();
				case FILL_WITH_BREEDING_METHOD_NUMBER:
					return ((Method) methodsMap.get(gid)).getMid().toString();
				case FILL_WITH_BREEDING_METHOD_GROUP:
					return ((Method) methodsMap.get(gid)).getMgrp();
				default:
					break;
			}
		}
		return "";
	}

	public void setCrossMaleGIDColumnValues(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();

		if (!itemIds.isEmpty()) {
			final Integer level = this.crossExpansionProperties.getCropGenerationLevel(this.pedigreeService.getCropName());
			final Table<Integer, String, Optional<Germplasm>> table = this.pedigreeDataManager.generatePedigreeTable(new HashSet<>(this.fillColumnSource.getGidsToProcess()), level, false);

			for (final Object itemId : itemIds) {
				final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
				final Optional<Germplasm> maleParent = table.get(gid, ColumnLabels.MGID.getName());
				final String value = BreedingManagerUtil.getGermplasmGid(maleParent);
				this.fillColumnSource.setColumnValueForItem(itemId, columnName, value);
			}

			this.fillColumnSource.propagateUIChanges();
		}
	}

	private ImmutableMap<Integer, Germplasm> retrieveGermplasmAndGenerateMap(final List<Integer> gids) {
		final List<Germplasm> germplasmList = this.germplasmDataManager.getGermplasms(gids);
		ImmutableMap<Integer, Germplasm> germplasmMap = null;
		if (germplasmList != null) {
			germplasmMap = Maps.uniqueIndex(germplasmList, new Function<Germplasm, Integer>() {

				@Override
				public Integer apply(final Germplasm germplasm) {
					return germplasm.getGid();
				}
			});
		}
		return germplasmMap;
	}

	public void setCrossMalePrefNameColumnValues(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			final Integer level = this.crossExpansionProperties.getCropGenerationLevel(this.pedigreeService.getCropName());
			final Table<Integer, String, Optional<Germplasm>> table = this.pedigreeDataManager.generatePedigreeTable(new HashSet<>(this.fillColumnSource.getGidsToProcess()), level, false);

			for (final Object itemId : itemIds) {
				final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
				final Optional<Germplasm> maleParent = table.get(gid, ColumnLabels.MGID.getName());
				final String value = BreedingManagerUtil.getGermplasmPreferredName(maleParent);
				this.fillColumnSource.setColumnValueForItem(itemId, columnName, value);
			}
			this.fillColumnSource.propagateUIChanges();
		}
	}

	public void setCrossFemaleInfoColumnValues(final String columnName, final FillWithOption option) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();

		if (!itemIds.isEmpty()) {

			final Integer level = this.crossExpansionProperties.getCropGenerationLevel(this.pedigreeService.getCropName());
			final Table<Integer, String, Optional<Germplasm>> table = this.pedigreeDataManager.generatePedigreeTable(new HashSet<>(this.fillColumnSource.getGidsToProcess()), level, false);

			for (final Object itemId : itemIds) {
				final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
				final Optional<Germplasm> femaleParent = table.get(gid, ColumnLabels.FGID.getName());
				if (femaleParent.isPresent()) {
					final String value;
					if (FillWithOption.FILL_WITH_CROSS_FEMALE_GID.equals(option)) {
						value = BreedingManagerUtil.getGermplasmGid(femaleParent);
					} else if (FillWithOption.FILL_WITH_CROSS_FEMALE_NAME.equals(option)) {
						value = BreedingManagerUtil.getGermplasmPreferredName(femaleParent);
					} else {
						value = "-";
					}
					this.fillColumnSource.setColumnValueForItem(itemId, columnName, value);

				} else {
					this.fillColumnSource.setColumnValueForItem(itemId, columnName, "-");
				}
			}
			this.fillColumnSource.propagateUIChanges();
		}
	}

	public void fillWithEmpty(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			for (final Object itemId : itemIds) {
				this.fillColumnSource.setColumnValueForItem(itemId, columnName, "");
			}

			this.fillColumnSource.propagateUIChanges();
		}
	}

	public void fillWithAttribute(final Integer attributeType, final String columnName) {
		if (attributeType != null) {
			final List<Integer> gids = this.fillColumnSource.getGidsToProcess();
			if (!gids.isEmpty()) {
				final Map<Integer, String> gidAttributeMap =
						this.germplasmDataManager.getAttributeValuesByTypeAndGIDList(attributeType, gids);

				final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
				for (final Object itemId : itemIds) {
					final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
					this.fillColumnSource.setColumnValueForItem(itemId, columnName, gidAttributeMap.get(gid));
				}
				this.fillColumnSource.propagateUIChanges();
			}
		}

	}

	public void fillWithGermplasmName(final Integer nameType, final String columnName) {
		if (nameType != null) {
			final List<Integer> gids = this.fillColumnSource.getGidsToProcess();
			if (!gids.isEmpty()) {
				final Map<Integer, String> gidNamesMap = this.germplasmDataManager.getNamesByTypeAndGIDList(nameType, gids);
				final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
				for (final Object itemId : itemIds) {
					final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
					this.fillColumnSource.setColumnValueForItem(itemId, columnName, gidNamesMap.get(gid));
				}
				this.fillColumnSource.propagateUIChanges();
			}
		}

	}

	public void fillWithSequence(final String columnName, final String prefix, final String suffix, final int startNumber,
								 final int numOfZeros, final boolean spaceBetweenPrefixAndCode, final boolean spaceBetweenSuffixAndCode) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			int number = startNumber;
			for (final Object itemId : itemIds) {
				final StringBuilder builder = new StringBuilder();
				builder.append(prefix);
				if (spaceBetweenPrefixAndCode) {
					builder.append(" ");
				}

				if (numOfZeros > 0) {
					final String numberString = "" + number;
					final int numOfZerosNeeded = numOfZeros - numberString.length();
					for (int i = 0; i < numOfZerosNeeded; i++) {
						builder.append("0");
					}
				}
				builder.append(number);

				if (suffix != null && spaceBetweenSuffixAndCode) {
					builder.append(" ");
				}

				if (suffix != null) {
					builder.append(suffix);
				}
				this.fillColumnSource.setColumnValueForItem(itemId, columnName, builder.toString());
				++number;
			}

			this.fillColumnSource.propagateUIChanges();
		}
	}

	public void fillWithCrossExpansion(final Integer crossExpansionLevel, final String columnName) {
		if (crossExpansionLevel != null) {

			final Map<Integer, String> crossExpansions = this.bulkGeneratePedigreeString(crossExpansionLevel);

			for (final Object o : this.fillColumnSource.getItemIdsToProcess()) {
				// iterate through the table elements' IDs
				final Integer listDataId = (Integer) o;
				final Integer gid = this.fillColumnSource.getGidForItemId(listDataId);
				final String crossExpansion = crossExpansions.get(gid);
				this.fillColumnSource.setColumnValueForItem(listDataId, columnName, crossExpansion);
			}

			this.fillColumnSource.propagateUIChanges();

		}
	}

	private Map<Integer, String> bulkGeneratePedigreeString(final Integer crossExpansionLevel) {

		final Set<Integer> gidIdList = new HashSet<>(this.fillColumnSource.getGidsToProcess());
		final Iterable<List<Integer>> partition = Iterables.partition(gidIdList, 5000);

		final Map<Integer, String> crossExpansions = new HashMap<>();

		for (final List<Integer> partitionedGidList : partition) {
			final Set<Integer> partitionedGidSet = new HashSet<>(partitionedGidList);
			crossExpansions.putAll(this.pedigreeService.getCrossExpansions(partitionedGidSet, crossExpansionLevel,
					this.crossExpansionProperties));
		}
		return crossExpansions;
	}

	public void setGermplasmDataManager(final GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

	public void setPedigreeService(final PedigreeService pedigreeService) {
		this.pedigreeService = pedigreeService;
	}

	public void setPedigreeDataManager(final PedigreeDataManager pedigreeDataManager) {
		this.pedigreeDataManager = pedigreeDataManager;
	}

	public void setCrossExpansionProperties(final CrossExpansionProperties crossExpansionProperties) {
		this.crossExpansionProperties = crossExpansionProperties;
	}

	public void setGroupSourceGidColumnValues(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			final Map<Integer, Germplasm> germplasmMap = this.getGermplasmMapByGid();
			for (final Object itemId : itemIds) {
				final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
				final Germplasm germplasm = germplasmMap.get(gid);
				final String groupSourceGid = germplasm.getGnpgs() == -1 && germplasm.getGpid1() != null && germplasm.getGpid1() != 0
						? germplasm.getGpid1().toString() : "-";
				this.fillColumnSource.setColumnValueForItem(itemId, columnName, groupSourceGid);
			}
			this.fillColumnSource.propagateUIChanges();
		}
	}

	public void setGroupSourcePreferredNameColumnValues(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			final List<Integer> gids = this.fillColumnSource.getGidsToProcess();
			final Map<Integer, String> gidAndPreferredNameMap = this.germplasmDataManager.getGroupSourcePreferredNamesByGids(gids);
			this.fillColumnsWithPreferredName(itemIds, gidAndPreferredNameMap, columnName);
		}
	}

	public void setImmediateSourceGidColumnValues(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			final Map<Integer, Germplasm> germplasmMap = this.getGermplasmMapByGid();
			for (final Object itemId : itemIds) {
				final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
				final Germplasm germplasm = germplasmMap.get(gid);
				final String immediateSourceGid = germplasm.getGnpgs() == -1 && germplasm.getGpid2() != null && germplasm.getGpid2() != 0
						? germplasm.getGpid2().toString() : "-";
				this.fillColumnSource.setColumnValueForItem(itemId, columnName, immediateSourceGid);
			}
			this.fillColumnSource.propagateUIChanges();
		}
	}

	public void setImmediateSourcePreferredNameColumnValues(final String columnName) {
		final List<Object> itemIds = this.fillColumnSource.getItemIdsToProcess();
		if (!itemIds.isEmpty()) {
			final List<Integer> gids = this.fillColumnSource.getGidsToProcess();
			final Map<Integer, String> gidAndPreferredNameMap = this.germplasmDataManager.getImmediateSourcePreferredNamesByGids(gids);
			this.fillColumnsWithPreferredName(itemIds, gidAndPreferredNameMap, columnName);
		}
	}

	private void fillColumnsWithPreferredName(final List<Object> itemIds, final Map<Integer, String> gidAndPreferredNameMap,
											  final String columnName) {
		for (final Object itemId : itemIds) {

			final Integer gid = this.fillColumnSource.getGidForItemId(itemId);
			final String preferredName = gidAndPreferredNameMap.get(gid);
			this.fillColumnSource.setColumnValueForItem(itemId, columnName, preferredName);
			this.fillColumnSource.propagateUIChanges();
		}
	}

	private Map<Integer, Germplasm> getGermplasmMapByGid() {
		final List<Integer> gids = this.fillColumnSource.getGidsToProcess();
		return Maps.uniqueIndex(this.germplasmDataManager.getGermplasms(gids), new Function<Germplasm, Integer>() {

			@Override
			public Integer apply(@Nullable final Germplasm germplasm) {
				return germplasm != null ? germplasm.getGid() : 0;

			}
		});
	}
}
