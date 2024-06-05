# Functionality

## Requirments
- List of teams with their starting locations
- List of assignments per location for teams and helpers
- List of locations
- Discord

## Flow
1. Game is started by the admin
```
/iamadmin <password>
```
2. Helpers at locations register themselves:
```
/iamhelper <id> <password>
```
3. Teams send a DM to the bot to register their team
```
/register <teamid>
```
4. Team gets their starting location
5. Helper unlocks assignments at location
```
/unlock <teamid>
```
6. Team receives assignments
7. Team submits assignments (with photo or video attached)
```
/submit <assignment id>
```
8. Team goes to location
```
/helper
```
9. Team receives next location, and starts at step 5 again
10. The game ends when the admin ends it or when the time is up
```
/endgame
```