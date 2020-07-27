package com.openclassrooms.realestatemanager;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.openclassrooms.realestatemanager.database.RealEstateManagerDatabase;
import com.openclassrooms.realestatemanager.models.Agent;
import com.openclassrooms.realestatemanager.models.Property;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class PropertyDaoTest {

    // FOR DATA
    private RealEstateManagerDatabase database;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDb() {
        this.database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                RealEstateManagerDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        database.close();
    }


    // DATA SET FOR TEST
    private static long AGENT_ID = 42;
    private static Agent AGENT_DEMO = new Agent(AGENT_ID,"ABC","Br");
    private static Property PROPERTY_DEMO = new Property(AGENT_ID, "Paris", 123);

    @Test
    public void getAgentListWhenNoAgentInserted() throws InterruptedException {
        List<Agent> agentList = LiveDataTestUtil.getValue(this.database.agentDao().getAgentList());
        assertTrue(agentList.isEmpty());
    }

    @Test
    public void insertAndGetAgent() throws InterruptedException {
        // BEFORE : add agent in db
        this.database.agentDao().createAgent(AGENT_DEMO);
        // TEST
        List<Agent> agentList = LiveDataTestUtil.getValue(this.database.agentDao().getAgentList());
        assertTrue(agentList.get(0).getId() == (AGENT_DEMO.getId())
                && agentList.get(0).getName().equals(AGENT_DEMO.getName())
                && agentList.get(0).getSurname().equals(AGENT_DEMO.getSurname()));
        assertEquals(1, agentList.size());
    }

    @Test
    public void insertAndUpdateAgent() throws InterruptedException{
        this.database.agentDao().createAgent(AGENT_DEMO);
        Agent agent = LiveDataTestUtil.getValue(this.database.agentDao().getAgent(AGENT_ID));
        agent.setSurname("Bruno");
        this.database.agentDao().updateAgent(agent);
        // Test
        Agent agentUpdated = LiveDataTestUtil.getValue(database.agentDao().getAgent(AGENT_ID));
        assertEquals("Bruno", agentUpdated.getSurname());
    }

    @Test
    public void insertAndDeleteAgent() throws InterruptedException{
        this.database.agentDao().createAgent(AGENT_DEMO);
        List<Agent> agentList = LiveDataTestUtil.getValue(this.database.agentDao().getAgentList());
        assertEquals(1, agentList.size());
        this.database.agentDao().deleteAgent(AGENT_DEMO);

        agentList = LiveDataTestUtil.getValue(this.database.agentDao().getAgentList());
        assertTrue(agentList.isEmpty());
    }

    @Test
    public void getPropertyListWhenNoPropertyInserted() throws InterruptedException {
        List<Property> propertyList = LiveDataTestUtil.getValue(database.propertyDao().getPropertyList());
        assertTrue(propertyList.isEmpty());
    }

    @Test
    public void insertAndGetProperty() throws InterruptedException{
        this.database.agentDao().createAgent(AGENT_DEMO);
        this.database.propertyDao().createProperty(PROPERTY_DEMO);

        List<Property> propertyList = LiveDataTestUtil.getValue(database.propertyDao().getPropertyList());
        assertTrue(propertyList.size() == 1
                && propertyList.get(0).getAgentId() == AGENT_DEMO.getId()
                && propertyList.get(0).getCity().equals("Paris")
                && propertyList.get(0).getPrice() == 123);
    }

    @Test
    public void insertAndUpdateProperty() throws InterruptedException{
        this.database.agentDao().createAgent(AGENT_DEMO);
        this.database.propertyDao().createProperty(PROPERTY_DEMO);
        Property property = LiveDataTestUtil.getValue(database.propertyDao().getPropertyList()).get(0);
        property.setCity("Marseille");
        property.setPrice(456);
        this.database.propertyDao().updateProperty(property);

        List<Property> propertyList = LiveDataTestUtil.getValue(database.propertyDao().getPropertyList());
        assertTrue(propertyList.size() == 1
                && propertyList.get(0).getCity().equals("Marseille")
                && propertyList.get(0).getPrice() == 456);
    }

}