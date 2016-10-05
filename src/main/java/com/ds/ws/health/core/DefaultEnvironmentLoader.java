package com.barclays.solveit.ws.health.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.barclays.solveit.ws.health.common.CoreConstants;
import com.barclays.solveit.ws.health.model.Environment;
import com.barclays.solveit.ws.health.model.EnvironmentDetail;
import com.barclays.solveit.ws.health.model.Provider;
import com.barclays.solveit.ws.health.model.Service;
import com.barclays.solveit.ws.health.util.WSHealthUtils;

/**
 * This class is the heart of ws-health-check.<br>
 * This class loads the {@link Environment}, {@link Provider} and {@link Service}
 * @author G09633463
 * @since 29/08/2016
 * @version 1.0
 */
@Component
@DependsOn(value="customTrustStoreManager")
public class DefaultEnvironmentLoader implements EnvironmentLoader {

	private static final Logger logger = LoggerFactory.getLogger(DefaultEnvironmentLoader.class); 

	@Autowired
	private Properties environmentProperties;

	@Autowired
	private Properties cmPropertyFileLocations;

	@Autowired
	private CoreConstants coreConstants;

	@Autowired
	private WSHealthUtils wsHealthUtils;

	private final Map<String,String> cmProperties = new HashMap<>();

	private final Map<String,String> componentVerions = new HashMap<>();

	private static final String CUSTOM_KEY_SEPARAOTR = ".";  

	private final Set<Environment> environments = new HashSet<>();

	// to track which env is up, if the env is down its details wont be loaded
	private final Set<String> environmentsUp = new HashSet<>();

	// Provider names. This must correspond to PROVIDER_NAME in environments.properties file.
	private static final String COMP_BEM = "BEM";
	private static final String COMP_BFH = "BFH";
	private static final String COMP_BPM = "BPM";
	private static final String COMP_CM = "CM";

	private final void init() {
		loadServiceUrls();
		loadVersions();
	}

	/**
	 * loads versions of all providers
	 */
	private void loadVersions() {
		loadCMVersions();
	}

	/**
	 * loads CM versions
	 */
	private void loadCMVersions() {
		logger.info("loading CM versions..");
		for (Entry<Object,Object> entry : cmPropertyFileLocations.entrySet()) {
			Assert.notNull(entry.getKey(), "Key in cm.properties file cannot be null");
			Assert.notNull(entry.getValue(), "Value in cm.properties file cannot be null");
			final String env = entry.getKey().toString();
			final String propFileLocation = entry.getValue().toString();

			try {
				File tmpCMPropFile = new File(System.getProperty("java.io.tmpdir") + propFileLocation.hashCode() +".txt");
				FileUtils.copyURLToFile(new URL(propFileLocation), tmpCMPropFile);
				//FileUtils.copyFile(new File(propFileLocation), tmpCMPropFile);
				String version = FileUtils.readLines(tmpCMPropFile).get(3).toString()
						.replaceAll("[^0-9]", "").substring(0, 4);
				componentVerions.put(env+CUSTOM_KEY_SEPARAOTR+COMP_CM, version);
				logger.debug("CM version for ENV {} is {}", env, version);
			} catch (IOException e) {
				logger.error("Failed to fetch CM version for ENV [{}]",env);
			}
		}
		logger.info("loading CM versions completed");

	}

	/**
	 * loads services urls of all the services.<br>
	 * The urls are loaded dynamically by reading the prop file of CM 
	 */
	private void loadServiceUrls() {
		logger.info("Loading Service URI's from CM properties file started...");
		for (Entry<Object,Object> entry : cmPropertyFileLocations.entrySet()) {
			Assert.notNull(entry.getKey(), "Key in cm.properties file cannot be null");
			Assert.notNull(entry.getValue(), "Value in cm.properties file cannot be null");
			final String env = entry.getKey().toString();
			final String propFileLocation = entry.getValue().toString();

			Properties properties = new Properties();

			try {
				logger.debug("Loading CM Property for ENV : {}",env);
				File tmpCMPropFile = new File(System.getProperty("java.io.tmpdir") + propFileLocation.hashCode() +".txt");
				FileUtils.copyURLToFile(new URL(propFileLocation), tmpCMPropFile);
				properties.load(new FileInputStream(tmpCMPropFile));
				environmentsUp.add(env);
			} catch (IOException e) {
				logger.error("Failed to fetch CM properties for ENV [{}]",env);
			}

			for (Entry<Object,Object> cmPropertyEntry : properties.entrySet()) {
				cmProperties.put(env+CUSTOM_KEY_SEPARAOTR+cmPropertyEntry.getKey(), cmPropertyEntry.getValue().toString());
				logger.debug("Property : {} | Value : {}", cmPropertyEntry.getKey(),cmPropertyEntry.getValue().toString());
			}

		}
		logger.info("Loading Service URI's from CM properties file completed");
	}

	@Override
	@Cacheable("environments")
	public final void loadEnvironments() {
		init();
		logger.info("Loading environmets started...");
		List<EnvironmentDetail> environmentDetails = getEnvDetailsViaProperties();
		for (EnvironmentDetail environmentDetailsView : environmentDetails) {
			logger.debug("loading env details for {}", environmentDetailsView);
			final String envName = environmentDetailsView.getEnvName();
			final String providerName = environmentDetailsView.getProviderName();
			final String providerVersion = environmentDetailsView.getProviderVer();
			final String desc = environmentDetailsView.getServiceDesc();
			final String uri = environmentDetailsView.getServiceUri();

			final Environment environment = new Environment(envName);
			logger.debug("Environment created {}",environment);
			final Provider provider = new Provider(providerName, envName, providerVersion);
			logger.debug("Provider created {}",provider);
			final Service serviceDetail = new Service(envName, providerName, desc, uri);
			logger.debug("Service created {}",serviceDetail);

			// check if env is already loaded. 
			if(environments.contains(environment)) {
				logger.debug("Environments Set contains ENV {}", envName);
				for (Environment env : environments) {
					if(env.equals(environment)) {
						// if env is already loaded get its components
						Set<Provider> envComponents = env.getComponents();

						logger.debug("Environment {} contains Components {}", envName, envComponents);
						// check if component is loaded in this env.
						if(envComponents.contains(provider)) {
							logger.debug("Env Components contains Component {}", provider);
							for (Provider component : envComponents) {
								if(component.equals(provider)) {
									// if yes then attach the service to it
									component.getServices().add(serviceDetail);
									logger.debug("Service {} added to Component {} in Env {}", serviceDetail,provider,envName);
									break;
								}
							}
						} else {
							// if component is not loaded in this env, then add this component to env components
							logger.debug("Env Components does not contain Component {}", provider);
							provider.getServices().add(serviceDetail);
							env.getComponents().add(provider);
							logger.debug("Service {} added to Component {} in Env {}", serviceDetail,provider,envName);
						}
						break;
					}
				}
			} else { // if not go ahead and load it
				logger.debug("Environments Set does not contain ENV {}", envName);
				provider.getServices().add(serviceDetail);
				environment.getComponents().add(provider);
				logger.debug("Service {} added to Component {} in Env {}", serviceDetail,provider,envName);
				environments.add(environment);
				logger.debug("Environment {} added to Environments Set", envName);
			}

		}
		logger.info("Loading environmets completed.");
	}

	/**
	 * Get the Env details from property file
	 * @return list {@link EnvironmentDetail}
	 */
	private List<EnvironmentDetail> getEnvDetailsViaProperties() {
		logger.debug("Fetching ENV details via properties...");
		final List<EnvironmentDetail> environmentDetails = new ArrayList<>();
		for (Entry<Object,Object> envDetailEntry : environmentProperties.entrySet()) {
			String[] envDetails = StringUtils.split(StringUtils.trimToEmpty(envDetailEntry.getValue().toString()),
					coreConstants.environmentDetailsSeparatorKey);

			Assert.isTrue(envDetails.length == 5,
					"environment details currently supports only 5 properties viz [enviornment,provider,provider_version,description,uri]");

			final String envName = envDetails[0];
			final String provider = envDetails[1];
			final String providerVersion = componentVerions.containsKey(envName + CUSTOM_KEY_SEPARAOTR + provider)
					? componentVerions.get(envName + CUSTOM_KEY_SEPARAOTR + provider) : "NA";

			final String desc = envDetails[3];
			final String uri = cmProperties.containsKey(envName + CUSTOM_KEY_SEPARAOTR + envDetails[4])
					? cmProperties.get(envName + CUSTOM_KEY_SEPARAOTR + envDetails[4]) : "NA"; // setting NA if env is down and property isn't loaded

			if(!environmentsUp.contains(envName))
				logger.debug("[{}] is down, correct details cannot not be loaded.",envName);

			EnvironmentDetail environmentDetail = new EnvironmentDetail.Builder(envName, provider,
					wsHealthUtils.cleanUrl(uri)).providerVer(providerVersion).serviceDesc(desc).build();
			environmentDetails.add(environmentDetail);

			logger.debug("ENV details fetched {}",environmentDetail);
			
		}
		logger.debug("Fetching ENV details via properties completed");
		return Collections.unmodifiableList(environmentDetails);
	}

	private DefaultEnvironmentLoader() {}

	@Override
	public Set<Environment> getEnvironments() {
		return Collections.unmodifiableSet(this.environments);
	}

}
