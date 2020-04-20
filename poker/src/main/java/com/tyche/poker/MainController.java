package com.tyche.poker;

import com.tyche.poker.model.PokerTable;
import com.tyche.poker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller // must be Controller (not RestController) for Thymeleaf...
public class MainController {


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private PokerTableRepository pokerTableRepository;


    @GetMapping(path="/")
    public String register() {
        return "register.html";
    }


    @GetMapping(path="/room")
    public String room(Model model,String uuid) {

        try{
            if (getUser(uuid) == null) throw new Exception();
            User thisUser = getUser(uuid);
            Iterable<User> currentUsersIterable = getAllUsers();
            List<User> otherUsers = StreamSupport.stream(currentUsersIterable.spliterator(), false).collect(Collectors.toList());
            otherUsers.remove(thisUser); // filter out your user from the list of other users

            try{
                PokerTable tableState = StreamSupport.stream(getAllTables().spliterator(), false).collect(Collectors.toList()).get(0);

                model.addAttribute("thisUser", thisUser);
                model.addAttribute("otherUsers", otherUsers);
                model.addAttribute("tableState", tableState);

            } catch(Exception e){
                PokerTable tableState = new PokerTable();
                tableState.setPot(0);
                tableState.setUuid("null");
                tableState.setFlop0("null");

                model.addAttribute("thisUser", thisUser);
                model.addAttribute("otherUsers", otherUsers);
                model.addAttribute("tableState", tableState);
                
            }

            return "room.html";
        } catch (Exception e){
            return "register.html";
        }
    }


    @PostMapping(path = "/users")
    public RedirectView newUser(@RequestBody String name) {
        String uuid = UUID.randomUUID().toString();
        User newUser = new User();
        newUser.setUuid(uuid);
        newUser.setName(name.substring(6));
        newUser.setChips(1000);
        newUser.setCard0("null");
        newUser.setCard1("null");
        userRepository.save(newUser);

        // when a new user is added, we want to update the view of users already in the game
        // how do we refresh the page of /room?uuid= of users in the DB?
        updateState();

        RedirectView rv = new RedirectView("/room");
        rv.addStaticAttribute("uuid", uuid);
        return rv;
    }


    public User getUser(String uuid) {
        return userRepository.findByUuid(uuid);
    }


    @GetMapping(path="/start")
    public @ResponseBody String startGame(){
        deleteAllTables();
        newRoundSetUp();
        return "Let the Hunger Games begin!";
    }


    @GetMapping(path="/users")
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }


    @GetMapping(path="/users/delete")
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }


    @GetMapping(path="/users/delete/{name}")
    public @ResponseBody String deleteUserByName(@PathVariable String name) {
            User userToExecute = userRepository.findByName(name);
            userRepository.delete(userToExecute);
            updateState();
            return "successfully wiped the user: " + name;
    }


    @GetMapping(path="/tables")
    public @ResponseBody Iterable<PokerTable> getAllTables() {
        return pokerTableRepository.findAll();
    }


    @GetMapping(path="/tables/delete")
    public void deleteAllTables() {
        pokerTableRepository.deleteAll();
    }


    @GetMapping(path="/flood")
    public @ResponseBody String flood(){
            deleteAllUsers();
            deleteAllTables();
            updateState();
            return "successfully wiped the game";
    }


    //Anytime that userRepository edits the DB, we need to refresh the state for each "room" perspective
    public void updateState(){
        System.out.println("state change");
        // trigger all open "room" perspectives to refresh
        // trigger refresh at the "room" level, regardless of of UUID
    }


    public void newRoundSetUp(){
        PokerTable table = new PokerTable();
        table.setPot(0);
        table.setUuid(UUID.randomUUID().toString());
        table.setFlop0(new Card().toString());
        pokerTableRepository.save(table);

        Iterable<User> currentUsersIterable = getAllUsers();
        List<User> currentUsers = StreamSupport.stream(currentUsersIterable.spliterator(), false).collect(Collectors.toList());

        currentUsers.get(0).setMyTurn(true); // first player always bet first, MVP

        for(User user : currentUsers){
            user.setCard0((new Card()).toString());
            userRepository.save(user);
        }

        updateState();
        // need to push these changes to the "room" views


        // the next step (for STEP#2 is to place bets

    }


    @PostMapping(path = "/turn")
    public RedirectView makeTurn(@RequestBody String uuid_action_betValue) {

        // extract uuid, action & betValue
        // uuid=eea3d544-1b9a-4e55-8a61-663a9cf9c99d&action=raise&betValue=10
        String uuid = uuid_action_betValue.substring(5,41);
        String action = uuid_action_betValue.substring(49,53);
        String betValue = uuid_action_betValue.substring(63);
        System.out.println(uuid);
        System.out.println(action);
        System.out.println(betValue);

        // update the state of this user and that of the table
        User thisUser = getUser(uuid);
        thisUser.setMyTurn(false);

        Iterable<PokerTable> thisTableIter = getAllTables();
        List<PokerTable> thisTableList = StreamSupport.stream(thisTableIter.spliterator(), false).collect(Collectors.toList());
        PokerTable thisTable = thisTableList.get(0);

        switch(action){
            case "fold":
                thisUser.setFold(true);
                break;
            case "call":
                thisUser.setMyBet(new PokerTable().getCurrentBet());
                thisUser.setChips(thisUser.getChips() - thisUser.getMyBet());
                thisTable.setCurrentBet(thisUser.getMyBet());
                thisTable.setPot(thisTable.getPot() + thisUser.getMyBet());
                break;
            case "chec":
                thisUser.setMyBet(0);
                break;
            case "rais":
                thisUser.setMyBet(Integer.parseInt(betValue));
                thisUser.setChips(thisUser.getChips() - thisUser.getMyBet());
                thisTable.setCurrentBet(thisUser.getMyBet());
                thisTable.setPot(thisTable.getPot() + thisUser.getMyBet());
                break;
        }

        // need to move the turn along to the next user...
        Iterable<User> allUsersIter = getAllUsers();
        List<User> allUsersList = StreamSupport.stream(allUsersIter.spliterator(), false).collect(Collectors.toList());
        int index = allUsersList.indexOf(thisUser);
        int len = allUsersList.size();
        if (index == len - 1){ // index = len - 1
            // end of turn, last user has been
            System.out.println("Need to end turn bitch!");
        } else{
            User nextUser = allUsersList.get(index + 1); // index = len - 1
            nextUser.setMyTurn(true);
            userRepository.save(nextUser);
        }

        userRepository.save(thisUser);
        pokerTableRepository.save(thisTable);


        updateState();


        // RETURN A REDIRECT BACK TO THE PAGE THIS REQUEST CAME FROM!
        RedirectView rv = new RedirectView("/room");
        rv.addStaticAttribute("uuid", uuid);
        return rv;

    }






}
