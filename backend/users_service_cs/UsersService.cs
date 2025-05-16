using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Internal;
using Microsoft.IdentityModel.Tokens;

public static class UsersService
{
    public class User
    {
        public User(int id, string username)
        {
            Id = id;
            Username = username;
        }

        public int Id { get; set; }
        public string Username { get; set; }
    }

    private class UserCredentials
    {
        public string? username { get; set; }
        public string? password { get; set; }
    }

    private static List<User> users = new List<User>() {
        new User(100, "admin"),
        new User(101, "User1"),
        new User(800, "Guest")
    };

    public class UsersDbContext : DbContext
    {
        public UsersDbContext(DbContextOptions<UsersDbContext> options) : base(options) {}

        public DbSet<User> Users { get; set; }
    }

    public static void Main(string[] args)
    {
        var builder = WebApplication.CreateBuilder(args);
        builder.Services.AddControllers();

        var dbConnectionString = "server=localhost;user=houen;password=houen;database=usersdatabase";
        builder.Services.AddDbContext<UsersDbContext>(options => options
            .UseMySql(dbConnectionString, new MariaDbServerVersion("11.7.2")));

        var app = builder.Build();

        using(var scope = app.Services.CreateScope())
        {
            var dbc = scope.ServiceProvider.GetService<UsersDbContext>();

            foreach(var user in users)
            {
                if(!dbc.Users.Contains(user))
                {
                    dbc.Users.Add(user);
                }
            }
            dbc.SaveChanges();
        }

        app.MapPost("/users/login", (UserCredentials credentials, UsersDbContext db) =>
        {
           var user = db.Users.FirstOrDefault(x => x.Username == credentials.username);
           if(user == null || credentials.username != credentials.password) {
              return Results.BadRequest($"User '{credentials.username}' not found or provided bad credentials");
           }

           var currentTimestamp = DateTime.UtcNow;
           var jwtToken = new JwtSecurityToken(
               notBefore: currentTimestamp,
               expires: currentTimestamp.AddHours(1),
               claims: new List<Claim> { new Claim(ClaimTypes.Name, user.Id.ToString()) },
               signingCredentials: new SigningCredentials(
                   new SymmetricSecurityKey(Encoding.UTF8.GetBytes("mysecretkeythatislongenoughforthehashingalgorithmmysecretkeythan")),
                   SecurityAlgorithms.HmacSha512Signature));

           return Results.Ok(new Dictionary<string, string> {
               { "username", user.Username },
               { "jwt", new JwtSecurityTokenHandler().WriteToken(jwtToken) }
           });
        });

        app.MapControllers();
        app.Run();
    }
}
