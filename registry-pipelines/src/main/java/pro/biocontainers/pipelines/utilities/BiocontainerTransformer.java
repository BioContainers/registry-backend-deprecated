package pro.biocontainers.pipelines.utilities;

import lombok.extern.log4j.Log4j;
import pro.biocontainers.data.model.ContainerType;
import pro.biocontainers.data.model.Tuple;
import pro.biocontainers.mongodb.model.BioContainerTool;
import pro.biocontainers.mongodb.model.BioContainerToolVersion;
import pro.biocontainers.mongodb.model.ContainerImage;
import pro.biocontainers.readers.IRegistryContainer;
import pro.biocontainers.readers.dockerhub.model.DockerHubContainer;
import pro.biocontainers.readers.utilities.dockerfile.models.DockerContainer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * ==Overview==
 * <p>
 * This class
 * <p>
 * Created by ypriverol (ypriverol@gmail.com) on 25/07/2018.
 */
@Log4j
public class BiocontainerTransformer {

    public static BioContainerTool transformContainerToBiocontainer(IRegistryContainer container, String accessionURL) {
//        String accession = accessionURL.replace("%%name_space%%", container.getNameSpace()).replace("%%software_name%%", container.getName());
//        List<ContainerImage> images = new ArrayList<>();
//        container.getContainerTags().stream().forEach( x-> {
//            images.add(ContainerImage.builder()
//                    .size(x.getValue())
//                    .tag(x.getKey())
//                    .build());
//        });
//        return  BioContainerTool.builder()
//                .name(container.getName())
//                .id(accession)
//                .description(container.getDescription())
////                .lastUpdate(container.getLastUpdated())
////                .pullCount(container.getPullCount())
////                .images(images)
//                .starred(container.isStarred())
//                .build();

        return null;
    }

    /**
     * Convert Docker Container to {@link BioContainerToolVersion}
     * @param container {@link DockerContainer}
     * @param accessionURL url
     * @return BioContainerToolVersion
     */
    public static Optional<BioContainerToolVersion> transformContainerToolVersionToBiocontainer(DockerContainer container,
                                                                                                List<DockerHubContainer> dockerHubContainers, String accessionURL) {
        // Parse the DockerContainers
        List<DockerHubContainer> finalContainers = new ArrayList<>();
        if(dockerHubContainers != null){
            for(DockerHubContainer hubContainer: dockerHubContainers){
                List<Tuple<String, Integer>> sameVersions = hubContainer
                        .getContainerTags()
                        .parallelStream()
                        .filter(x -> x.getKey().toLowerCase().contains(container.getVersion()))
                        .collect(Collectors.toList());
                if(sameVersions.size() > 0){
                    finalContainers.add(hubContainer);
                }
            }
        }

        List<ContainerImage> containerImages = new ArrayList<>();
        if(finalContainers.size() > 0){
            finalContainers.forEach(x -> x.getContainerTags().forEach(y -> {
                ContainerImage containerImage = ContainerImage.builder()
                        .size(y.getValue())
                        .accession(y.getKey())
                        .description(container.getDescription())
                        .containerType(ContainerType.DOCKER)
                        .lastUpdate(x.getLastUpdated())
                        .tag(y.getKey())
                        .build();
                containerImages.add(containerImage);
            }));
        }

        List<DockerHubContainer> finalUpdates = finalContainers.stream().filter(x -> x.getLastUpdated() != null)
                .sorted(Comparator.comparing(DockerHubContainer::getLastUpdated)).collect(Collectors.toList());
        Date finalUpdate = null;
        if(finalContainers.stream().findFirst().isPresent())
            finalUpdate = finalContainers.stream().findFirst().get().getLastUpdated();

        if(container.getSoftwareName() == null)
            log.error("Not name for contain -- ");
        return Optional
                .of(BioContainerToolVersion
                        .builder()
                        .name(container.getSoftwareName())
                        .version(container.getSoftwareVersion())
                        .description(container.getDescription())
                        .isContainerRecipeAvailable(true)
                        .isVerified(false)
                        .lastUpdate(finalUpdate)
                        .hashName(GeneralUtils.getHashName(container.getSoftwareName()))
                        .containerImages(containerImages)
                        .license(container.getLicense())
                        .additionalIdentifiers(container
                                .getExternalIds()
                                .entrySet()
                                .stream()
                                .map(x -> new Tuple<>(x.getKey(), x.getValue()))
                                .collect(Collectors.toList()))
                        .build());



    }
}
