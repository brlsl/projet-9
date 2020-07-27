package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.dao.AgentDao;
import com.openclassrooms.realestatemanager.models.Agent;

import java.util.List;

public class AgentDataRepository {
    AgentDao agentDao;

    public AgentDataRepository(AgentDao agentDao){
        this.agentDao = agentDao;
    }

    public void createAgent(Agent agent){
        agentDao.createAgent(agent);
    }

    public LiveData<Agent> getAgent(long id){
        return agentDao.getAgent(id);
    }

    public LiveData<List<Agent>> getAgentList(){
        return agentDao.getAgentList();
    }

    public void updateAgent(Agent agent){
        agentDao.updateAgent(agent);
    }

    public void deleteAgent(Agent agent){
        agentDao.deleteAgent(agent);
    }

}